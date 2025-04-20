package com.multitenacydemo.service;

import com.multitenacydemo.config.TenantContext;
import com.multitenacydemo.config.tenant.TenantDataSourceProvider;
import com.multitenacydemo.masterdb.TenantEntity;
import com.multitenacydemo.masterdb.TenantRepository;
import com.multitenacydemo.repo.UserRepository;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.cfg.AvailableSettings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TenantService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final TenantDataSourceProvider tenantDataSourceProvider;
    @Value("${spring.datasource.username}")
    private String DB_USERNAME;

    @Value("${spring.datasource.password}")
    private String DB_PASSWORD;

    @Value("${spring.datasource.url}")
    private String DB_URL;

    private final TenantRepository tenantRepository;


    public TenantService(TenantRepository tenantRepository, UserRepository userRepository, UserService userService, TenantDataSourceProvider tenantDataSourceProvider) {
        this.tenantRepository = tenantRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.tenantDataSourceProvider = tenantDataSourceProvider;
    }

    public List<TenantEntity> getAllTenants() {
        return tenantRepository.findAll();
    }

    public TenantEntity createTenant(String tenantName, String username) {
        boolean isExists = tenantRepository.existsByTenantId(tenantName);
        if((tenantName == null || username == null) || isExists) {
            System.out.println("\n\n\n"+ isExists +"\n\n\n");
            return null;
        }

        TenantEntity tenantEntity = new TenantEntity();
        tenantEntity.setTenantId(tenantName);
        tenantEntity.setJdbcUrl(DB_URL+tenantName);
        tenantEntity.setUsername(DB_USERNAME);
        tenantEntity.setPassword(DB_PASSWORD);
        tenantEntity.setStatus("ACTIVE");

        createTenantDatabase(tenantName);

//        Method 1
//        initializeTenantSchema(tenantName);
        tenantRepository.save(tenantEntity);

        TenantContext.setTenantId(tenantName);

//        Method 2
        tenantDataSourceProvider.getDataSource(tenantName);
        triggerHibernateDDL(tenantName);

        userService.createUser(username);

        return tenantEntity;
    }


    public void createTenantDatabase(String tenantId) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             Statement statement = connection.createStatement()) {

            statement.executeUpdate("CREATE DATABASE IF NOT EXISTS " + tenantId);
            System.out.println("Database " + tenantId + " created or already exists.");

        } catch (SQLException e) {
            throw new RuntimeException("Error creating tenant DB: " + e.getMessage(), e);
        }
    }

//    Method 1
//    public void initializeTenantSchema(String tenantId) {
//        String tenantDbUrl = DB_URL + tenantId;
//
//        try (Connection connection = DriverManager.getConnection(tenantDbUrl, DB_USERNAME, DB_PASSWORD);
//             Statement statement = connection.createStatement()) {
//
//            String createUserTable = "CREATE TABLE IF NOT EXISTS user (" +
//                    "id BIGINT AUTO_INCREMENT PRIMARY KEY," +
//                    "name VARCHAR(255)" +
//                    ")";
//            statement.executeUpdate(createUserTable);
//            System.out.println("Schema initialized for " + tenantId);
//
//        } catch (SQLException e) {
//            throw new RuntimeException("Error initializing tenant schema: " + e.getMessage(), e);
//        }
//    }


//    Method 2
    public void triggerHibernateDDL(String tenantId) {
        TenantContext.setTenantId(tenantId); // critical: makes routingDataSource pick the new DS

        DataSource dataSource = tenantDataSourceProvider.getDataSource(tenantId);

        // Bootstrap EntityManagerFactory with hbm2ddl.auto=create
        LocalContainerEntityManagerFactoryBean emfBean = new LocalContainerEntityManagerFactoryBean();
        emfBean.setDataSource(dataSource);
        emfBean.setPackagesToScan("com.multitenacydemo.entity");
        emfBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        Map<String, Object> jpaProperties = new HashMap<>();
        jpaProperties.put(AvailableSettings.DIALECT, "org.hibernate.dialect.MySQL8Dialect");
        jpaProperties.put(AvailableSettings.HBM2DDL_AUTO, "create"); // << force create
        emfBean.setJpaPropertyMap(jpaProperties);

        emfBean.afterPropertiesSet(); // Initializes EMF and triggers schema generation
        emfBean.getObject().close();  // Optional: Close EMF if not needed further
    }


}
