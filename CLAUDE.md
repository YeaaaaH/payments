# Payments API (duo)

Personal budgeting REST API: record spending by category, get monthly/yearly reports.
**Multi-user app**: every user has their own username and password (auth will later move to
Google OAuth). Self-hosted on a VPS. The frontend lives in a separate repo (React, being rebuilt);
this repo is backend only. Roadmap: `docs/ROADMAP.md`.

## Working rules (strict)
1. **Never commit without explicit approval from the user.**
2. **Never delete or rewrite existing Liquibase changesets** — schema changes go in a new changeset.
3. **Keep it multi-user** — don't remove per-user accounts, signup or ownership checks.
4. **Small steps** — one small change at a time, a short summary, then wait for the user.
5. **Commit messages** — a short description of what changed, a few words (no body).

## Stack
- Java 17, Spring Boot 2.5.4 (Web, Data JPA, Security, Validation), Gradle 7.2 wrapper
- PostgreSQL + Liquibase migrations, Lombok
- JWT auth via `com.auth0:java-jwt`, Swagger via springfox 2.9.2 (legacy, to be replaced by springdoc)
- Tests: JUnit 5 + Testcontainers (Postgres), integration-style only

An upgrade to current Spring Boot / Java LTS is planned (Phase 2 of the roadmap) — until then keep
code compatible with Boot 2.5 (`javax.*`, `WebSecurityConfigurerAdapter`).

## Commands
- Build: `./gradlew build`
- Compile only (fast check, no Docker): `./gradlew compileJava compileTestJava`
- Tests: `./gradlew test` — **requires Docker running** (Testcontainers starts `postgres:13.3`)
- Single test class: `./gradlew test --tests "payments.duo.integration.service.IntegrationUserServiceTest"`
- Local stack: `./gradlew build && docker compose up --build` (API on :8080, Postgres on :5433)
- Swagger UI: http://localhost:8080/swagger-ui.html

Required env vars to run: `DATABASE_URL`, `USERNAME`, `PASSWORD`, `SECRET` (JWT), `EXPIRE_TIME` (ms).

## Layout (`src/main/java/payments/duo/`)
- `controller/` — REST endpoints under `/api/v1/**` (authenticated) and `/api/auth/**` (public)
- `service/` interfaces + `service/impl/` implementations
- `repository/` — Spring Data JPA repositories
- `model/` — JPA entities (`Payment`, `Category`, `auth/User`, `auth/Role`);
  `model/request/` — incoming `*Command` / `*Request` objects; `model/response/` — `*Response` / `*DTO`
- `security/` — `SecurityConfig`, `JwtAuthorizationFilter`, `JwtTokenProvider`
- `exception/` — custom exceptions + `CustomizedEntityExceptionHandler` (`@RestControllerAdvice`)
- `utils/` — `Constants` (messages), `UserFactory` (entity → DTO/JwtUser mapping), validators
- `src/main/resources/db/changelog/` — Liquibase changelog; `changeSet/NNN-description.xml`

## Conventions
- Constructor injection (no field `@Autowired`); inject service **interfaces**, not `*Impl` classes
- Never return JPA entities from controllers — map to a response DTO
- Request objects are `*Command` classes with Bean Validation annotations; controllers use `@Valid`
- Error/validation messages go in `utils/Constants`
- Money is `BigDecimal`; payment dates are `LocalDate`

## Database migrations
- **Never edit a changeset that has already been applied** — add a new `NNN-*.xml` file and
  include it in `changelog.xml`
- `spring.jpa.hibernate.ddl-auto=none`: Liquibase owns the schema; entities must match it

## Tests
- Integration tests in `src/test/java/payments/duo/integration/`, profile `test`
  (`application-test.properties`), DB from a Testcontainers Postgres via `@DynamicPropertySource`
- Tests share a DB container per class — use unique usernames/emails per test
