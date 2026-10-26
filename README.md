# Payments

Personal budgeting REST API: record your spending by category and see where the money goes each month
and year. Multi-user — every user has their own account and sees only their own data. Self-hosted.

This repository is the backend only; the web UI (React) lives in a separate repository.
Technical details: [ARCHITECTURE.md](ARCHITECTURE.md).

## What you can do

**Account**
- Sign up with a username, email and password; sign in to get an access token.
- View and update your own profile (name, email, username).

**Spending**
- Add a payment: amount, category, date and time it was paid, optional title and description.
- Add many payments in one request (batch import).
- Edit or delete your payments.
- List your payments for a given month or year.

**Reports**
- Monthly and yearly totals per category, e.g. `{"Food": 4200.50, "Taxi": 830.00}`.
- Months and years follow your configured time zone (default Kyiv), so a payment at 00:30 on the 1st
  belongs to the new month.

**Categories**
- A shared list of 14 categories: Food, Health and Beauty, Clothes, Household products, Education,
  Entertainment, Sports, Subscriptions, Cafes and Restaurants, Taxi, Loans and credits, Savings, Pets,
  Repairment.

Amounts must be positive, with at most 2 decimal places.

## API overview

All `/api/v1/**` endpoints need the header `Authorization: Bearer <token>` from sign-in.
Full, interactive docs: Swagger UI at http://localhost:8080/swagger-ui.html.

| Method | Path | Description |
|---|---|---|
| POST | `/api/auth/signup` | Create an account |
| POST | `/api/auth/signin` | Sign in, returns a JWT |
| GET | `/api/v1/user` | Current user's profile |
| GET / PUT | `/api/v1/user/{id}` | Read / update own profile |
| GET | `/api/v1/category` | All categories |
| GET | `/api/v1/category/{id}` | One category |
| POST | `/api/v1/payment` | Add a payment |
| POST | `/api/v1/payment/saveAll` | Add several payments |
| GET / PUT / DELETE | `/api/v1/payment/{id}` | Read / update / delete a payment |
| GET | `/api/v1/payment/list/monthly?year=&month=` | Payments for a month |
| GET | `/api/v1/payment/list/yearly?year=` | Payments for a year |
| GET | `/api/v1/payment/report/monthly?year=&month=` | Totals per category for a month |
| GET | `/api/v1/payment/report/yearly?year=` | Totals per category for a year |

Accessing another user's payment or profile returns 404.

## Running locally

Prerequisites: Java 17, Docker (Gradle comes with the wrapper).

1. Copy the env template and fill in the values: `cp .env.example .env` (`.env` is git-ignored).
2. Build (compiles and runs the tests, needs Docker running):
   ```
   ./gradlew build
   ```
3. Start Postgres and the API:
   ```
   docker compose up --build
   ```
   API on http://localhost:8080, Postgres on `localhost:5432`.

To run the API from the IDE instead, start only the database (`docker compose up postgres`) and pass the
variables from `.env` to the run configuration.

### Environment variables

| Variable | Required | Description |
|---|---|---|
| `DATABASE_URL` | yes | JDBC URL, e.g. `jdbc:postgresql://localhost:5432/postgres` |
| `DB_USERNAME` | yes | DB user |
| `DB_PASSWORD` | yes | DB password |
| `SECRET` | yes | JWT signing secret |
| `EXPIRE_TIME` | yes | Token lifetime in ms |
| `APP_TIMEZONE` | no | Time zone for month/year boundaries, default `Europe/Kyiv` |

Variables for the upcoming Telegram/Google sign-in are already listed in `.env.example` but not used yet.

## Development

- Tests: `./gradlew test` (integration tests with Testcontainers; Docker required).
- Fast compile check without Docker: `./gradlew compileJava compileTestJava`.
- CI runs `./gradlew build` on every pull request to `master`.
- One branch per task (`feature/…`, `fix/…`, `task/…`), merged through a pull request.

## Roadmap

- Sign-in through a Telegram bot with Google-verified email, invite-only sign-up; record spending
  straight from Telegram.
- Google sign-in for the web UI; passwords removed.
- Spring Boot and Java upgrade.
