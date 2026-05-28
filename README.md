# Bill Splitter API 
REST API for splitting restaurant bills among friends. Built with Java 21, Spring Boot 4, Spring Security + JWT, and PostgreSQL.

### Features 
- JWT authentication with register and login
- Create and manage orders with tax and tip configuration
- Add participants to orders
- Add items and assign them to participants
- Automatic bill calculation with per-participant breakdown
- Role-based access control (creator vs participant)

### Tech Stack 
- Java 21 
- Spring Boot 4 
- Spring Security + JWT 
- Spring Data JPA / Hibernate 
- PostgreSQL 
- Maven 

### Environment Variables 
```bash
DB_NAME = your_database_name  
DB_USERNAME = your_database_user 
DB_PASSWORD = your_database_password 
JWT_SECRET = yout_jwt_secret 
```
Generate a JWT secret with 
```bash
openssl rand -base64 32
```