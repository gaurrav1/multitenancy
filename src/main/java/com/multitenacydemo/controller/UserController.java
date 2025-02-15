package com.multitenacydemo.controller;

import java.util.List;

import com.multitenacydemo.config.TenantContext;
import com.multitenacydemo.config.hibernate.TenantSchemaResolver;
import com.multitenacydemo.model.DBDetails;
import com.multitenacydemo.model.User;
import com.multitenacydemo.security.RegisterRequest;
import com.multitenacydemo.service.UserSvc;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.sql.DataSource;


@RestController
@RequestMapping("/multiTenantApp")
public class UserController {

	@Autowired
	UserSvc userSvc;
    @Autowired
    private TenantSchemaResolver tenantSchemaResolver;

	@GetMapping("/user")
	public ResponseEntity<List<User>> getUser() {
		List<User> user = userSvc.getUser();
		return new ResponseEntity<>(user, HttpStatus.OK);
	}


	@GetMapping("/users")
	public ResponseEntity<List<DBDetails>> getUsers() {

		List<DBDetails> users = userSvc.getAllUsers();
		return new ResponseEntity<>(users, HttpStatus.OK);
	}

	@PostMapping("/register")
	public ResponseEntity<?> registerTenantEntity(@RequestBody RegisterRequest registerRequest) {
		return userSvc.registerTenant(registerRequest);
	}

	@GetMapping("/delete")
	public ResponseEntity<List<DBDetails>> delete() {

		System.out.println(TenantContext.getCurrentTenant());
		System.out.println(tenantSchemaResolver.resolveCurrentTenantIdentifier());
		deleteDatabase(new JdbcTemplate(createDataSource("MASTER_DB")));

		List<DBDetails> users = userSvc.getAllUsers();
		return new ResponseEntity<>(users, HttpStatus.OK);
	}

	private void deleteDatabase(JdbcTemplate jdbcTemplate) {
		try {
			jdbcTemplate.execute("DROP DATABASE IF EXISTS TENANT_DB_4");
			jdbcTemplate.execute("DELETE FROM TENANT_DB_LIST WHERE DATABASE_ID='1005'");
		} catch (Exception e) {
			throw new RuntimeException("Error Deleting tenant database: " + e.getMessage(), e);
		}
	}

	private DataSource createDataSource(String dbName) {
		return DataSourceBuilder.create()
				.url("jdbc:mysql://localhost:3306/" + dbName)
				.username("gaurav")
				.password("NGaurav@113")
				.driverClassName("com.mysql.cj.jdbc.Driver")
				.type(HikariDataSource.class) // Use HikariCP for connection pooling
				.build();
	}

}
