# Tasks: Hotel Booking Membership Discounts & Cancellation Policy

This file breaks down the implementation into actionable, dependency-ordered tasks, following the provided specifications and best practices.

---

## Phase 1: Setup Tasks

**T001. [Setup] Initialize project repository and environment**
- Clone repo, checkout feature branch, 
- Initialize Spring Boot project with dependencies (Spring Web, Spring Data JPA, PostgreSQL Driver, Flyway/Liquibase, OpenAPI) ensure Java 17+, PostgreSQL 14+, Docker, Gradle(build.gradle.kts)..
- Configure DB connection in `application.yml`/`properties`.

**T002. [X] Add and configure Flyway/Liquibase for DB migrations**
- Set up migration scripts for initial schema.

**T003. [Setup] Set up CI pipeline for build, test, and code style**
- Add GitHub Actions workflow for build, test, lint.

---

## Phase 2: Foundational Tasks

**T004. [X] Implement base domain entities and repositories**
- User, Room, Booking, Cancellation entities (per data-model.md).
- JPA repositories for each.

**T005. [X] Implement authentication (member login)**
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

**T010. [X] Implement invoice/summary logic**
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

## Phase 6: User Story 4 (P2) — View Membership Level and Benefits

**Goal:** Users can view their current membership level and see all available benefits/discounts.

**T024. [US4] Implement endpoint for users to view their membership level and benefits**
- Returns current user's membership level and discount rate, plus a summary of all levels/benefits.

**T025. [US4] Unit and integration tests for membership/benefits endpoint**

---

## Phase 7: User Story 5 (P1) — Users Can View and Cancel Only Their Own Bookings

**Goal:** Users can only view and cancel bookings they own.

**T026. [US5] Implement endpoint to list user's own bookings**

**T027. [US5] Restrict booking cancellation to booking owner**
- Ensure users cannot view or cancel bookings they do not own.

**T028. [US5] Unit and integration tests for booking ownership and access control**

---

## Phase 8: User Story 6 (P2) — Admin Can Create Users and Set/Update Membership Level

**Goal:** Admins can create users and set/update membership levels.

**T029. [US6] Implement admin endpoint to create users and set membership**

**T030. [US6] Implement admin endpoint to update user membership level**

**T031. [US6] Unit and integration tests for admin user/membership management**

---

## Phase 9: User Story 7 (P2) — Admin Can View All Bookings

**Goal:** Admins can view all bookings in the system.

**T032. [US7] Implement admin endpoint to list all bookings**

**T033. [US7] Unit and integration tests for admin booking listing and access control**

---

## Final Phase: Polish & Cross-Cutting Concerns

**T021. [Polish] Add structured logging and error handling**
- JSON error responses, no stack traces.

**T022. [Polish] Add health endpoints and observability**

**T023. [Polish] Finalize OpenAPI docs and code style checks**

---

## Dependencies & Parallelization

- T001–T003 (Setup) → T004–T006 (Foundational) → US1 (T007–T011) → US2 (T012–T016) → US3 (T017–T020) → US4 (T024–T025) → US5 (T026–T028) → US6 (T029–T031) → US7 (T032–T033) → Polish (T021–T023)
- Tasks within each user story phase can be done in parallel.

---

## MVP Scope

- Complete through US1 (T001–T011): Booking with membership discount, tested and documented.
