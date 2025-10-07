# Tasks: Hotel Booking Membership Discounts & Cancellation Policy

This file breaks down the implementation into actionable, dependency-ordered tasks, following the provided specifications and best practices.

---

## Phase 1: Setup Tasks

**T001. [Setup] Initialize project repository and environment**
- Clone repo, checkout feature branch, 
- Initialize Spring Boot project with dependencies (Spring Web, Spring Data JPA, PostgreSQL Driver, Flyway/Liquibase, OpenAPI) ensure Java 17+, PostgreSQL 14+, Docker, Gradle(build.gradle.kts)..
- Configure DB connection in `application.yml`/`properties`.

**T002. [Setup] Add and configure Flyway/Liquibase for DB migrations**
- Set up migration scripts for initial schema.

**T003. [Setup] Set up CI pipeline for build, test, and code style**
- Add GitHub Actions workflow for build, test, lint.

---

## Phase 2: Foundational Tasks

**T004. [Foundational] Implement base domain entities and repositories**
- User, Room, Booking, Cancellation entities (per data-model.md).
- JPA repositories for each.

**T005. [Foundational] Implement authentication (member login)**
- Basic authentication for all endpoints.

**T006. [Foundational] Set up OpenAPI/Swagger documentation**
- Auto-generate docs for all endpoints.

---

## Phase 3: User Story 1 (P1) — Membership Discount Applied to Booking

**Goal:** Members receive correct discount on booking; reflected in summary and invoice.

**T007. [US1] Implement BookingRequest/BookingResponse DTOs**

**T008. [US1] Implement booking creation service logic**
- Apply membership discount (classic/gold/diamond: 0/10/20%).

**T009. [US1] Implement POST /api/v1/bookings endpoint**

**T010. [US1] Implement invoice/summary logic**
- Ensure discount is itemized in response.

**T011. [US1] Unit and integration tests for booking with discounts**
- Test all membership levels.

---

## Phase 4: User Story 2 (P2) — Booking Cancellation with Refund Policy

**Goal:** Users can cancel bookings and receive correct refund/penalty.

**T012. [US2] Implement CancellationResponse DTO**

**T013. [US2] Implement cancellation service logic**
- Apply refund policy: >48h=100%, 24-48h=50%, <24h=0%.

**T014. [US2] Implement POST /api/v1/bookings/{bookingId}/cancel endpoint**

**T015. [US2] Integrate refund processing (simulate payment gateway)**

**T016. [US2] Unit and integration tests for cancellation/refund**
- Test all policy tiers.

---

## Phase 5: User Story 3 (P3) — Calculate Refund/Penalty Before Cancelling

**Goal:** Users can preview refund/penalty before cancelling.

**T017. [US3] Implement RefundPreviewResponse DTO**

**T018. [US3] Implement refund/penalty calculation service**

**T019. [US3] Implement GET /api/v1/bookings/{bookingId}/refund-preview endpoint**

**T020. [US3] Unit and integration tests for refund preview**

---

## Final Phase: Polish & Cross-Cutting Concerns

**T021. [Polish] Add structured logging and error handling**
- JSON error responses, no stack traces.

**T022. [Polish] Add health endpoints and observability**

**T023. [Polish] Finalize OpenAPI docs and code style checks**

---

## Dependencies & Parallelization

- T001–T003 (Setup) → T004–T006 (Foundational) → US1 (T007–T011) → US2 (T012–T016) → US3 (T017–T020) → Polish (T021–T023)
- Tasks within each user story phase can be done in parallel.

---

## MVP Scope

- Complete through US1 (T001–T011): Booking with membership discount, tested and documented.

---


