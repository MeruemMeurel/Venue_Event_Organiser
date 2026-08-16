# Venue Event Manager

University Software Engineering project for managing venues, event requests, published events, bookings, tickets, private guest lists, resources, reviews and moderation reports.

The project intentionally focuses on domain modelling, service-layer business rules, persistence and software-engineering documentation. It currently exposes no graphical interface or production web API.

## Architecture

The application follows a layered design:

Production code uses the conventional lowercase root package `venue.event.manager`.

```text
Domain models
    ↑
Services and business rules
    ↑
Repository interfaces
    ↑
PostgreSQL JDBC repositories
    ↑
HikariCP, Flyway and PostgreSQL
```

Important design choices include:

- immutable domain models with `with...` copy methods;
- service layer responsible for validation and state transitions;
- repository pattern separating persistence contracts from PostgreSQL implementations;
- explicit read-write and read-only transaction management;
- row-level locking for booking capacity and competing lifecycle transitions;
- PBKDF2 password hashing with random salts;
- versioned Flyway migrations;
- JUnit 5, Mockito and PostgreSQL integration tests;
- JaCoCo coverage reporting.

## Requirements

- Java 17 or newer;
- Maven 3.9 or the Maven installation bundled with IntelliJ IDEA;
- Docker with Docker Compose, or a local PostgreSQL instance;
- Git.

The project is compiled with Java release 17. Running it with a newer JDK may produce warnings from build dependencies, but the current test suite supports the locally used JDK.

## Database setup

Create the local environment file:

```bash
cp .env.example .env
```

The values in `.env.example` are development-only defaults. Do not reuse them in a production environment.

Start PostgreSQL:

```bash
docker compose up -d postgres
```

Apply every Flyway migration:

```bash
mvn flyway:migrate
```

Flyway defaults are declared once as Maven properties. Override them when needed, for example:

```bash
mvn flyway:migrate -Ddb.host=localhost -Ddb.port=5433 -Ddb.name=event_manager_db -Ddb.user=admin -Ddb.password=change_me
```

The default local database is available at `localhost:5433`, database `event_manager_db`. Application settings may be overridden through these environment variables:

- `DB_HOST`
- `DB_PORT`
- `DB_NAME`
- `DB_USER`
- `DB_PASSWORD`
- `DB_SSLMODE`
- `DB_SCHEMA`

Flyway migrations are stored in `src/main/resources/db/migration`. Never edit an already applied migration; create a new numbered migration instead.

## Development seed accounts

The seed migration creates two local demonstration accounts:

| Role | Username | Password |
|---|---|---|
| Administrator | `admin_mario` | `Admin123!` |
| Standard user | `user_luigi` | `User1234!` |

These credentials exist only for local development and demonstrations. Passwords are stored in the database as PBKDF2 hashes, not as plaintext.

## Running the tests

Run the complete suite:

```bash
mvn clean test
```

The suite contains unit, service workflow, repository integration, transaction and concurrency tests. PostgreSQL integration tests are skipped through JUnit assumptions when the local database is unavailable. Start the database and apply the migrations to execute them.

Run one test class:

```bash
mvn -Dtest=EventServiceWorkflowTest test
```

## Coverage report

The JaCoCo report is generated automatically by the test lifecycle:

```bash
mvn clean test
```

Open the generated report:

```text
target/site/jacoco/index.html
```

Coverage is used as supporting evidence, not as a replacement for meaningful assertions and business-rule tests.

## JavaDoc

Generate the API documentation with:

```bash
mvn clean javadoc:javadoc
```

Open:

```text
target/reports/apidocs/index.html
```

The build runs JavaDoc with documentation checks enabled.

## Main business rules

- Newly created events start in `DRAFT`.
- Only allowed event and request state transitions are accepted.
- Only `PUBLISHED` events can be booked.
- Banned users cannot create bookings.
- Private events require an invitation.
- Event capacity is protected by transactions and row-level locking.
- Cancelling an event also cancels active bookings and guest entries atomically.
- A user can review an event only after attending it and only once.
- Administrative account flags cannot be changed through ordinary profile updates.
- New passwords and password changes are always stored as PBKDF2 credentials.

## Project status

The deliverable currently includes:

- domain and persistence implementation;
- service-layer workflows and validation;
- PostgreSQL schema, migrations and development seeds;
- password security and authorization checks;
- automated tests and coverage reporting;
- complete JavaDoc generation.

A small CLI may be added later as a demonstration aid, but it is intentionally lower priority than the engineering report, diagrams and traceability of the design decisions.
