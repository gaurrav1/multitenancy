package com.multitenacydemo.service;

import java.util.List;

import com.multitenacydemo.config.RegisterDataSource;
import com.multitenacydemo.config.TenantContext;
import com.multitenacydemo.model.DBDetails;
import com.multitenacydemo.model.User;
import com.multitenacydemo.repo.DataSourceConfigRepository;
import com.multitenacydemo.repo.UserRepo;
import com.multitenacydemo.security.RegisterRequest;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;


@Service
public class UserSvc {

	@Autowired
	UserRepo userRepo;
    @Autowired
    private DataSourceConfigRepository dataSourceConfigRepository;
	@Autowired
	RegisterDataSource registerDataSource;
	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	@PersistenceContext
	private EntityManager entityManager;

	public List<DBDetails> getAllUsers() {
		TenantContext.clear();
		return dataSourceConfigRepository.findAll();
	}

	public List<User> getUser() {


		TenantContext.setCurrentTenant("TENANT_DB_1");
		List<User> users = userRepo.findAll();
		TenantContext.setCurrentTenant("TENANT_DB_2");
		users.addAll(userRepo.findAll());
		return users;
	}

	public ResponseEntity<?> registerTenant(RegisterRequest registerRequest) {
		TenantContext.setCurrentTenant("MASTER_DB");
		DBDetails dbDetails = new DBDetails();
		dbDetails.setDbId("1005");
		dbDetails.setDbName(registerRequest.getDbName());
		dbDetails.setUrl(registerRequest.getUrl());
		dbDetails.setDbPwd(registerRequest.getDbPwd());

		createDatabaseForTenant(registerRequest.getUrl());

		registerDataSource.registerTenantDataSource(registerRequest.getUrl());

		TenantContext.setCurrentTenant(registerRequest.getUrl());

		try {
			registerUser();
		} catch (Exception e) {
			System.out.println("\n\n -------------Cant save user to new DB----------- \n"+ e.getMessage() +"\n\n");
			return new ResponseEntity<>("---Tenant registered, but user not saved", HttpStatus.CONFLICT);
		}

		dataSourceConfigRepository.save(dbDetails);
		return new ResponseEntity<>("Tenant registered successfully", HttpStatus.OK);
	}

	public Boolean registerUser() {
		User newUser = new User();
		newUser.setUserId("1006");
		newUser.setUserName("Uknown");
		newUser.setPhoneNo("8283618236");
		newUser.setEmailId("hehe@gmail.com");
		userRepo.save(newUser);
		return true;
		
	}

	public String createDatabaseForTenant(String tenantId) {

		if (!isValidTenantName(tenantId)) {
			return "Error: Invalid tenant name.";
		}

		String createDatabaseQuery = String.format("CREATE DATABASE IF NOT EXISTS %s", tenantId);

		try {
			jdbcTemplate.execute(createDatabaseQuery);
			return "Database created successfully.";
		} catch (Exception e) {
			return "Error: Failed to create tenant database.";
		}
	}

	private boolean isValidTenantName(String tenantId) {
		return tenantId != null && tenantId.matches("^[a-zA-Z0-9_-]+$");
	}

}
