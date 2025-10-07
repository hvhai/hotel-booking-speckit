# Quickstart: Hotel Booking Membership Discounts & Cancellation Policy

**Date**: 2025-10-07
**Feature**: /specs/001-hotel-booking-application/spec.md

## Prerequisites
- Java 17+
- PostgreSQL 14+
- Docker (for Testcontainers)
- Gradle (gradlew wrapper included and build.gradle.kts provided)

## Setup
1. Clone the repository and checkout the feature branch:
   ```sh
   git checkout 001-hotel-booking-application
   ```
2. Configure PostgreSQL connection in `application.yml` or `application.properties`.
3. Run database migrations (Flyway or Liquibase recommended).

## Running the Application
```sh
./gradlew bootRun
```

## API Endpoints
- `POST /api/v1/bookings` — Create a booking (membership discount auto-applied)
- `POST /api/v1/bookings/{bookingId}/cancel` — Cancel a booking (refund/penalty calculated)
- `GET /api/v1/bookings/{bookingId}/refund-preview` — Preview refund/penalty for a booking

## Testing
- Run all tests (unit, integration, contract):
  ```sh
  ./gradlew test
  ```
- Integration tests use Testcontainers for isolated Postgres DB

## Documentation
- OpenAPI/Swagger docs auto-generated at `/swagger-ui.html` or `/v3/api-docs`

## Notes
- All endpoints require authentication (login as a member)
- Only members (classic/gold/diamond) can book
- Refunds are processed automatically via payment gateway
- System is designed for at least 100 concurrent users and 1000 bookings/day (scalable)

