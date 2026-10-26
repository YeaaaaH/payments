# Payments API (duo)

Personal budgeting REST API: record spending by category, get monthly/yearly reports.
**Multi-user app**: every user has their own username and password (auth will later move to
Google OAuth). Self-hosted on a VPS. The frontend lives in a separate repo (React, being rebuilt);
this repo is backend only. Roadmap: `docs/ROADMAP.md`; open design questions (e.g. roles):
`docs/PLAN.md`. `docs/` is git-ignored (local only).

## Working rules (strict)
1. **Never commit without explicit approval from the user.**
2. **Never delete or rewrite existing Liquibase changesets** — schema changes go in a new changeset.
3. **Keep it multi-user** — don't remove per-user accounts, signup or ownership checks.
4. **Small steps** — one small change at a time, a short summary, then wait for the user.
5. **Commit messages** — `<N> - <short description>`: `N` is a running commit number (first one `30`,
   then +1 each commit — take the highest leading number in `git log` and add 1); the description is a
   few clear words about what changed. No body, no attribution trailers. Example: `30 - Load .env for local run`.

## Task workflow (GitHub issues)
Work starts from a GitHub issue written by the user (template `.github/ISSUE_TEMPLATE/task.md`:
Goal, Acceptance criteria, Out of scope, Notes).
1. The user says "take #N" -> read it with `gh issue view N --comments` and check it against the code.
2. Analysis: run the `issue-analyst` subagent (`.claude/agents/issue-analyst.md`, read-only) on the issue,
   check its result and present it in chat: plan of small steps, open questions, risks. After the user
   approves, post the plan as a comment on the issue (`gh issue comment N`). Never write to GitHub
   without the user's approval.
3. Branch `feature/<N>-<short-name>` (or `fix/`, `task/`) from an up-to-date `master`.
4. Implement step by step (working rules above); docs are updated in the same branch.
5. PR description contains `Closes #N`, so merging closes the issue.
Work without an issue (small chores the user asks for directly) uses a branch without the number.

## Git workflow
- **One branch per task/feature**, created from an up-to-date `master` at the start of the task:
  `feature/<N>-<short-kebab-name>`, `fix/<...>`, `task/<...>` (docs, config, CI, dependencies, other
  non-feature work); `<N>` is the issue number when there is one.
  Never commit to `master` directly. Commits still need the user's approval (rule 1).
- **Preparing a pull request** (when the task is done):
  1. `./gradlew build` passes locally (compile + tests; needs Docker — if it can't run, say so).
  2. Review `git diff master...HEAD`: no secrets (`.env`, tokens), no debug leftovers, no unrelated changes.
  3. Schema changes are new changesets included in `changelog.xml`.
  4. New/renamed env vars are in `.env.example`, this file and `README.md`; docs match changed commands.
     `README.md` (user-facing features, API, running) and `ARCHITECTURE.md` (technical design) describe
     the current state — update them in the same branch as any change they cover.
  5. Branch is up to date with `master` (ask before a rebase or force-push).
  6. Draft the PR title and description (what and why, how it was tested, follow-ups, `Closes #N` for the
     issue) and show it to the user; push and `gh pr create` only after approval.
- **CI** (`.github/workflows/ci.yml`) runs `./gradlew build` on pull requests to `master` and on pushes
  to `master`; merge only when it is green.

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
- Tests: `./gradlew test` — **requires Docker running** (Testcontainers starts `postgres:16-alpine`)
- Single test class: `./gradlew test --tests "payments.duo.integration.service.IntegrationUserServiceTest"`
- Local stack: `./gradlew build && docker compose up --build` (API on :8080, Postgres on :5432)
- Swagger UI: http://localhost:8080/swagger-ui.html

Required env vars to run: `DATABASE_URL`, `DB_USERNAME`, `DB_PASSWORD`, `SECRET` (JWT), `EXPIRE_TIME` (ms).
Optional: `APP_TIMEZONE` (default `Europe/Kyiv`) — zone for month/year boundaries of payment lists and reports.

## Layout (`src/main/java/payments/duo/`)
- `controller/` — REST endpoints under `/api/v1/**` (authenticated) and `/api/auth/**` (public)
- `service/` interfaces + `service/impl/` implementations
- `repository/` — Spring Data JPA repositories
- `model/` — JPA entities (`Payment`, `Category`, `auth/User`, `auth/Role`);
  `model/request/` — incoming `*Command` / `*Request` objects; `model/response/` — `*Response` / `*DTO`
- `security/` — `SecurityConfig`, `JwtAuthorizationFilter`, `JwtTokenProvider`, `AuthenticatedUser`
  (request principal built from the JWT: `userId` claim + username)
- `exception/` — custom exceptions + `CustomizedEntityExceptionHandler` (`@RestControllerAdvice`)
- `utils/` — `Constants` (messages), `UserFactory` (entity → DTO/JwtUser mapping), validators
- `src/main/resources/db/changelog/` — Liquibase changelog; `changeSet/NNN-description.xml`

## Conventions
- Constructor injection (no field `@Autowired`); inject service **interfaces**, not `*Impl` classes
- Never return JPA entities from controllers — map to a response DTO
- Request objects are `*Command` classes with Bean Validation annotations; controllers use `@Valid`
- Error/validation messages go in `utils/Constants`
- Money is `BigDecimal`, DB `NUMERIC(12,2)`; requests are validated with `@Digits(integer = 10, fraction = 2)`
- Payment times are `Instant` (DB `TIMESTAMP WITH TIME ZONE`): `paidAt` is entered by the user,
  `createdAt`/`updatedAt` are audit columns set by Hibernate. Month/year periods are half-open
  `[from, to)` ranges computed in `app.timezone`
- **Current user**: take it from `@AuthenticationPrincipal AuthenticatedUser` — never from a request
  param or body. Another user's payment/profile is treated as missing → 404

## Database migrations
- **Never edit a changeset that has already been applied** — add a new `NNN-*.xml` file and
  include it in `changelog.xml`
- `spring.jpa.hibernate.ddl-auto=none`: Liquibase owns the schema; entities must match it

## Tests
- Integration tests in `src/test/java/payments/duo/integration/`, profile `test`
  (`application-test.properties`), DB from a Testcontainers Postgres via `@DynamicPropertySource`
- Extend `AbstractIntegrationTest`: one Postgres container shared by all test classes; `payments`,
  `user_roles`, `users` are truncated after each test (categories/roles seed data is kept);
  `authHeaders(User)` gives a valid Bearer token for a saved user
