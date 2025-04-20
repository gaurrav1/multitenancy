package com.multitenacydemo.config;

import com.multitenacydemo.config.tenant.TenantDataSourceProvider;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;

public class MultiTenantRoutingDataSource extends AbstractRoutingDataSource {

    private final TenantDataSourceProvider dataSourceProvider;

    public MultiTenantRoutingDataSource(TenantDataSourceProvider dataSourceProvider) {
        this.dataSourceProvider = dataSourceProvider;
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return TenantContext.getTenantId();
    }

    @Override
    protected DataSource determineTargetDataSource() {
        String tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new IllegalStateException("No tenant ID set in TenantContext");
        }

        DataSource dataSource = dataSourceProvider.getDataSource(tenantId);
        if (dataSource == null) {
            throw new IllegalStateException("No DataSource found for tenant: " + tenantId);
        }

        return dataSource;
    }
}
