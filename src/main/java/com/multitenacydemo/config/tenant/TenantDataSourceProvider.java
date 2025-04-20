package com.multitenacydemo.config.tenant;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.multitenacydemo.masterdb.TenantRegistryService;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Component
public class TenantDataSourceProvider {

    private final TenantRegistryService registryService;

    private final Cache<String, DataSource> tenantDataSources = CacheBuilder.newBuilder()
            .expireAfterAccess(30, TimeUnit.MINUTES)
            .removalListener(notification -> {
                if (notification.getValue() instanceof HikariDataSource ds) {
                    ds.close();
                }
            })
            .build();

    public TenantDataSourceProvider(TenantRegistryService registryService) {
        this.registryService = registryService;
    }

    public DataSource getDataSource(String tenantId) {
        try {
            return tenantDataSources.get(tenantId, () -> {
                TenantRegistryService.TenantInfo info = registryService.getTenantInfo(tenantId);
                HikariConfig config = new HikariConfig();
                config.setDriverClassName("com.mysql.cj.jdbc.Driver");
                config.setJdbcUrl(info.url());
                config.setUsername(info.username());
                config.setPassword(info.password());
                config.setPoolName("Tenant-" + tenantId);
                return new HikariDataSource(config);
            });
        } catch (ExecutionException e) {
            throw new RuntimeException("Cannot create datasource for tenant: " + tenantId, e);
        }
    }
}
