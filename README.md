# Updated README with local run instructions

# SupaBackend (complete)

This branch adds migrations, docker-compose, refresh tokens and RBAC endpoints.

Quick start (local):
1. Copy .env.example to .env and adjust values.
2. Start Postgres via docker-compose:
   docker-compose up -d
3. Build & run the app:
   mvn spring-boot:run

Endpoints:
- POST /api/auth/register
- POST /api/auth/login -> returns { token, refreshToken }
- POST /api/auth/refresh { refreshToken } -> returns new token + refreshToken
- POST /api/auth/logout { refreshToken } -> revokes
- GET /api/test/protected (requires Authorization header)
- Admin endpoints (require ROLE_ADMIN): /api/admin/roles, /api/admin/users/{username}/roles
