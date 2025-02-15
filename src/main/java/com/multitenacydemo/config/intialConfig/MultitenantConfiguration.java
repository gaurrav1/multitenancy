package com.multitenacydemo.config.intialConfig;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class MultitenantConfiguration {

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Bean
    @ConfigurationProperties(prefix = "tenants")
    public AbstractRoutingDataSource dataSource() {
        Map<Object, Object> resolvedDataSources = new HashMap<>();
        String defaultTenant = "MASTER_DB";

        DataSource defaultDataSource = createDataSource(defaultTenant);
        resolvedDataSources.put(defaultTenant, defaultDataSource);

        loadExistingTenants(resolvedDataSources);

        AbstractRoutingDataSource dataSource = new MultiTenantDatasource();
        dataSource.setDefaultTargetDataSource(resolvedDataSources.get(defaultTenant));
        dataSource.setTargetDataSources(resolvedDataSources);

        dataSource.afterPropertiesSet();
        return dataSource;
    }

    private void loadExistingTenants(Map<Object, Object> resolvedDataSources) {
        try (Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             Statement stmt = connection.createStatement()) {
            var rs = stmt.executeQuery("SHOW DATABASES LIKE 'TENANT_DB_%'");

            while (rs.next()) {
                String tenantDbName = rs.getString(1);
                resolvedDataSources.put(tenantDbName, createDataSource(tenantDbName));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error loading tenant databases", e);
        }
    }

    private DataSource createDataSource(String dbName) {
        return DataSourceBuilder.create()
                .url(dbUrl + dbName)
                .username(dbUsername)
                .password(dbPassword)
                .driverClassName("com.mysql.cj.jdbc.Driver")
                .type(HikariDataSource.class)
                .build();
    }
}