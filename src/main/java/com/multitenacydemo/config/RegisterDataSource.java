package com.multitenacydemo.config;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class RegisterDataSource {

    private final AbstractRoutingDataSource multiTenantDatasource;

    public RegisterDataSource(AbstractRoutingDataSource multiTenantDatasource) {
        this.multiTenantDatasource = multiTenantDatasource;
    }

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    public synchronized void registerTenantDataSource(String tenantId) {
        String dbName = tenantId;
        DataSource dataSource = createDataSource(dbName);

        Map<Object, Object> resolvedDataSources = new HashMap<>(multiTenantDatasource.getResolvedDataSources());
        resolvedDataSources.put(dbName, dataSource);

        multiTenantDatasource.setTargetDataSources(resolvedDataSources);
        multiTenantDatasource.afterPropertiesSet();

        initializeDatabase(new JdbcTemplate(dataSource));
    }

    private void initializeDatabase(JdbcTemplate jdbcTemplate) {
        try {
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS USERS ("
                    + "USER_ID VARCHAR(100) PRIMARY KEY,"
                    + "USER_NAME VARCHAR(200),"
                    + "PHONE_NO VARCHAR(200),"
                    + "EMAIL_ID VARCHAR(200))");
        } catch (Exception e) {
            throw new RuntimeException("Error initializing tenant database: " + e.getMessage(), e);
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