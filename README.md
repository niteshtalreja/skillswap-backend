# SkillSwap Backend

Hyperlocal skill exchange platform — barter skills with other users. Built with Spring Boot + MySQL + JWT.

## Setup

1. **Create the database** (schema is auto-created by Hibernate, `ddl-auto=update`):
   ```sql
   CREATE DATABASE skillswap_db;
   ```

2. **Add dependencies to `pom.xml`** (same core set you already used in Hiredly):
   - `spring-boot-starter-web`
   - `spring-boot-starter-data-jpa`
   - `spring-boot-starter-security`
   - `spring-boot-starter-validation`
   - `mysql-connector-j`
   - `lombok`
   - `io.jsonwebtoken:jjwt-api:0.11.5`
   - `io.jsonwebtoken:jjwt-impl:0.11.5` (runtime scope)
   - `io.jsonwebtoken:jjwt-jackson:0.11.5` (runtime scope)

3. **Update `application.properties`**: set your MySQL username/password and a strong `jwt.secret` (32+ chars).

4. **Run** the app — Hibernate will create all tables automatically on first run.

## API Endpoints

### Auth (public)
| Method | Endpoint | Body |
|---|---|---|
| POST | `/api/auth/register` | `{ name, email, password, city, bio }` |
| POST | `/api/auth/login` | `{ email, password }` |

Both return `{ token, user }`. Send the token as `Authorization: Bearer <token>` on all routes below.

### Skills
| Method | Endpoint | Auth | Body |
|---|---|---|---|
| GET | `/api/skills` | public | — |
| POST | `/api/skills/offer` | required | `{ skillName }` |
| POST | `/api/skills/want` | required | `{ skillName }` |
| DELETE | `/api/skills/offer/{skillId}` | required | — |
| DELETE | `/api/skills/want/{skillId}` | required | — |
| GET | `/api/skills/my-offers` | required | — |
| GET | `/api/skills/my-wants` | required | — |

### Matches
| Method | Endpoint | Auth |
|---|---|---|
| GET | `/api/matches` | required | Returns list of `{ matchedUserId, matchedUserName, matchedUserCity, skillName }` — every user who offers a skill you want. |

## How matching works

1. You add skills you can teach to your **offer** list, and skills you want to learn to your **want** list.
2. `GET /api/matches` looks at every skill in your want list and finds other users who have that same skill in their offer list.
3. No AI needed for MVP — it's a straightforward SQL join. You can layer smarter ranking (location distance, ratings) on top later.

## Next steps (frontend)

- Reuse the `jobService.js` pattern from Hiredly: create `authService.js`, `skillService.js`, `matchService.js` under `src/services/`.
- Pages needed: `Login`, `Register`, `Profile` (add offer/want skills), `Matches` (list of matched users).
- Store JWT the same way you did in Hiredly (localStorage + Axios interceptor + `ProtectedRoute`).
