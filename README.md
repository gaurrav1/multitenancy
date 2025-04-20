#  Multi-Tenant Spring Boot Application

A practical Spring Boot project demonstrating **Multi-Tenancy** using:
- AbstractRoutingDataSource
- Dynamic tenant registration
- Hibernate auto-DDL schema generation
- Database per tenant approach

---

## Features

- Registering new tenant database & its user schema at runtime
- Separate MySQL database per tenant  
- Auto-generates schema using Hibernate's hbm2ddl.auto for newly registered tenant
- HikariCP for efficient connection pooling  
- Guava cache to manage tenant DataSources  
- Thread-local context switching for tenant routing

---
## Tech Stack
- Spring Boot
- Spring Data JPA
- Hibernate
- MySQL
- HikariCP
- Guava Cache by Google
- Maven
---

---
## How to start?
1. Create master database in your MySQL Server.
   ```mysql
   CREATE DATABASE YourMasterDbName
   ```
2. Update application.properties with your MySQL credential and your master-db url for `MASTER DATABASE CONFIG`.
   ```properties
   # TENANT CONFIG #
   spring.datasource.url=jdbc:mysql://localhost:3306/
   spring.datasource.username=<YourMySQLUsername>
   spring.datasource.password=<YourMySQLPassword>
   
   # MASTER DATABASE CONFIG #
   spring.datasource.master.jdbc-url=jdbc:mysql://localhost:3306/<YourMasterDbName>
   spring.datasource.master.username=<YourMySQLUsername>
   spring.datasource.master.password=<YourMySQLPassword>
   ... 
   ```
3. Start the application.
   ```shell
   ./mvnw spring-boot:run
   ```
---

## License
- MIT
