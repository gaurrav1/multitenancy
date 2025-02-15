package com.multitenacydemo.config.hibernate;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import jakarta.persistence.EntityManagerFactory;
import org.hibernate.cfg.Environment;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(
		basePackages = "com.multitenacydemo",
		entityManagerFactoryRef = "customEntityManagerFactory",
		transactionManagerRef = "customTransactionManager"
)
@EnableTransactionManagement
public class HibernateMultiTenancyConfig {

	private final JpaProperties jpaProperties;

	public HibernateMultiTenancyConfig(JpaProperties jpaProperties) {
		this.jpaProperties = jpaProperties;
	}

	@Bean
	public HibernateJpaVendorAdapter jpaVendorAdapter() {
		return new HibernateJpaVendorAdapter();
	}

	@Bean(name = "customEntityManagerFactory")
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(
			DataSource masterDataSource,
			TenantConnectionProvider tenantConnectionProvider,
			TenantSchemaResolver tenantIdentifierResolver) {

		HibernateJpaVendorAdapter vendorAdapter = this.jpaVendorAdapter();
		vendorAdapter.setGenerateDdl(false);

		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setJpaVendorAdapter(vendorAdapter);
		em.setPackagesToScan("com.multitenacydemo");
		em.setDataSource(masterDataSource);

		Map<String, Object> jpaPropertiesMap = new HashMap<>(jpaProperties.getProperties());
		jpaPropertiesMap.put(Environment.MULTI_TENANT_CONNECTION_PROVIDER, tenantConnectionProvider);
		jpaPropertiesMap.put(Environment.MULTI_TENANT_IDENTIFIER_RESOLVER, tenantIdentifierResolver);
		jpaPropertiesMap.put(Environment.FORMAT_SQL, true);
		jpaPropertiesMap.put("hibernate.hbm2ddl.auto", "none");
		jpaPropertiesMap.put("hibernate.show_sql", true);
		jpaPropertiesMap.put("hibernate.cache.use_second_level_cache", false);
		jpaPropertiesMap.put("hibernate.cache.use_query_cache", false);

		em.setJpaPropertyMap(jpaPropertiesMap);

		return em;
	}

	@Bean(name = "customTransactionManager")
	public PlatformTransactionManager transactionManager(@Qualifier("customEntityManagerFactory") EntityManagerFactory entityManagerFactory) {

		JpaTransactionManager txManager = new JpaTransactionManager();
		txManager.setEntityManagerFactory(entityManagerFactory);
		return txManager;
	}
}
