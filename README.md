# DriveLogix Vehicle Rental App

Desktop vehicle rental application built with Java Swing on the client side and Spring Boot on the backend side.

## Features

- Lists only currently available vehicles.
- Shows rented vehicles on a separate tab.
- Filters vehicles by `brand`, `model`, `class`, and maximum daily `price`.
- Uses Spring Data JPA with H2 for local development and PostgreSQL for real deployments.
- Changes vehicle status to `RENTED` after a successful rental.
- Allows returning vehicles only on or after one day after the rental period ends.
- Applies dynamic pricing:
  - `SHORT_TERM`: under 7 days, standard daily price
  - `LONG_TERM_WEEKLY`: 7 to 29 days, 10% discount
  - `LONG_TERM_30_PLUS`: 30+ days, 20% discount

## Local Run

```bash
mvn spring-boot:run
```

This starts the Spring Boot backend on `http://localhost:8080` and opens the Swing desktop application.

To run the local Swing client against the VPS backend using the local `.env` file:

```bash
set -a
source .env
set +a
mvn spring-boot:run
```

## Local Profiles

- Default profile: `h2`
- Production/VPS profile: `postgres`

Run backend only with PostgreSQL:

```bash
APP_BACKEND_ENABLED=true \
APP_SWING_ENABLED=false \
SPRING_PROFILES_ACTIVE=postgres \
DB_HOST=your-vps-db-host \
DB_PORT=5432 \
DB_NAME=drivelogix \
DB_USERNAME=drivelogix \
DB_PASSWORD=change-me \
mvn spring-boot:run
```

Run Swing client only against a remote VPS backend:

```bash
APP_BACKEND_ENABLED=false \
APP_SWING_ENABLED=true \
APP_API_BASE_URL=http://your-vps-ip:8080 \
mvn spring-boot:run
```

## VPS Database Setup

Example PostgreSQL install on Ubuntu:

```bash
sudo apt update
sudo apt install postgresql postgresql-contrib -y
sudo -u postgres psql
```

Inside `psql`:

```sql
CREATE DATABASE drivelogix;
CREATE USER drivelogix WITH ENCRYPTED PASSWORD 'change-me';
GRANT ALL PRIVILEGES ON DATABASE drivelogix TO drivelogix;
```

Then start the backend on the VPS with the `postgres` profile and the matching `DB_*` environment variables above.

## Public Repo Safety

- Commit `.env.example`, never `.env`
- Keep production secrets only on the server in `/etc/drivelogix/drivelogix.env`
- Use the `postgres` profile only via environment variables

## API

- `GET /api/vehicles`
  - Query params: `brand`, `model`, `class`, `maxPrice`
- `GET /api/vehicles/rented`
- `POST /api/vehicles/{vehicleId}/pricing`
- `POST /api/vehicles/{vehicleId}/rent`
- `POST /api/vehicles/rentals/{rentalId}/return`

## Test

```bash
mvn clean test
```
