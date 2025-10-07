# Research: Hotel Booking Membership Discounts & Cancellation Policy

**Date**: 2025-10-07
**Feature**: /specs/001-hotel-booking-application/spec.md

## Research Tasks

### 1. Clarify Scale/Scope
- Task: Research typical and peak load for hotel booking systems (users, bookings, concurrency) to inform scalability and performance goals.
- Decision: Design for horizontal scalability and stateless REST APIs. Use connection pooling and async processing for high concurrency. Set initial performance goal: support at least 100 concurrent users and 1000 bookings/day, but revisit as real data emerges.
- Rationale: Industry benchmarks for small/mid-size hotel platforms suggest these are safe starting points. Cloud-native design allows future scaling.
- Alternatives: Hardcode for low scale (rejected: not future-proof), overprovision for enterprise (rejected: unnecessary cost/complexity).

### 2. Performance Goals
- Task: Validate <200ms p95 response time for booking/cancellation endpoints.
- Decision: Target <200ms p95 for all endpoints under normal load, with stress testing in CI using Testcontainers and JUnit.
- Rationale: This is a common SaaS API target and aligns with user expectations for transactional systems.
- Alternatives: Higher latency (rejected: poor UX), lower (rejected: not justified by current scale).

### 3. Best Practices for Stack
- Task: Find best practices for Spring Boot 3, Spring Web, Spring Data JPA, PostgreSQL, JUnit, Mockito, Testcontainers in RESTful hotel booking domain.
- Decision: Use layered architecture (controller/service/repository), DTOs for API, OpenAPI for docs, Flyway for DB migrations, Testcontainers for integration tests, Mockito for unit tests, and CI pipeline with code style/linting.
- Rationale: These are industry standards for maintainability, testability, and compliance with the constitution.
- Alternatives: Monolithic service (rejected: less maintainable), no DB migrations (rejected: error-prone), no contract tests (rejected: risk of API drift).

## Consolidated Findings
- Design for at least 100 concurrent users and 1000 bookings/day, but keep system cloud-native and scalable.
- Target <200ms p95 response time for all endpoints.
- Use layered Spring Boot architecture, DTOs, OpenAPI, Flyway, Testcontainers, Mockito, and CI best practices.
- Revisit scale and performance goals as real usage data becomes available.

