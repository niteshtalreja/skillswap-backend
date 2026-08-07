# SupaBackend (Spring Boot 4.1.0 + Java 21 + PostgreSQL + JWT)

Quick start:
1. Ensure you have Java 21 and Maven.
2. Set environment variables:
   - DB_HOST, DB_PORT, DB_NAME, DB_USERNAME, DB_PASSWORD (or edit application.yml)
   - JWT_SECRET (must be long & random)
3. Build & run:
   mvn spring-boot:run

API:
- POST /api/auth/register
  body: { "username":"user", "email":"u@example.com", "password":"pass" }
- POST /api/auth/login
  body: { "username":"user", "password":"pass" }
  returns: { "token": "..." }
- GET /api/test/protected
  Header: Authorization: Bearer <token>

Notes:
- This project uses Hibernate JPA (ddl-auto:update). For production, use migrations (Flyway/Liquibase).
- For Supabase, use the DB connection info from your Supabase project (host, db name, user, password).
- Keep JWT_SECRET safe (use vault/secret manager in prod).
