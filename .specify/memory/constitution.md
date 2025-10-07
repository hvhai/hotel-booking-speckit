<!--
Sync Impact Report
------------------
Version change: undefined → 1.0.0
Modified principles: all added (RESTful Design, Spring Boot & JPA Standardization, Test-Driven Development, API Documentation & Contract, Observability & Error Handling)
Added sections: Technical Constraints, Development Workflow
Removed sections: none
Templates requiring updates:
- plan-template.md: ✅ generic, no update needed
- spec-template.md: ✅ generic, no update needed
- tasks-template.md: ✅ generic, no update needed
- commands/: ⚠ not found, none updated
- README.md: ⚠ not found
- docs/quickstart.md: ⚠ not found
Follow-up TODOs:
- TODO(RATIFICATION_DATE): original adoption date unknown, must be set on first ratification
-->

# Hotel Booking API Constitution

## Core Principles

### I. RESTful Design
All endpoints MUST follow REST conventions, use standard HTTP verbs, and provide clear resource-oriented URIs. Resource representations are exchanged in JSON. Hypermedia is not required, but consistent URI structure and statelessness are mandatory.

### II. Spring Boot & JPA Standardization
All business logic MUST be implemented using Spring Boot 3. Persistence MUST use Spring Data JPA. The only supported database is PostgreSQL. No other frameworks or databases are permitted for core service logic or persistence.

### III. Test-Driven Development
All features MUST be developed using TDD. Tests are written before implementation. Coverage includes unit, integration, and API contract tests. No code is merged without passing tests and review. Red-Green-Refactor cycle is strictly enforced.

### IV. API Documentation & Contract
Every endpoint MUST be documented using OpenAPI/Swagger. API contracts are versioned. Any breaking change requires a new version and explicit review. Documentation must be updated with every change.

### V. Observability & Error Handling
All services MUST provide structured logging, health endpoints, and error responses in JSON format. No stack traces or sensitive information may be exposed in error responses. Logs must be sufficient for debugging and monitoring.

## Technical Constraints
Only Java 17+ is permitted. Spring Boot 3.x and Spring Data JPA are mandatory. PostgreSQL 14+ is the only supported database. All dependencies must be open source and approved by the project maintainers. No proprietary or unsupported libraries are allowed.

## Development Workflow
All code changes require pull request review. Continuous Integration (CI) must pass all tests before merge. Deployments are only permitted from the main branch. Code style is enforced via Checkstyle and Spotless. All contributors must follow the constitution and technical constraints.

## Governance
This constitution supersedes all other practices. Amendments require a pull request, review, and a migration plan if breaking. All PRs must verify compliance with the constitution. Complexity must be justified. The constitution version must be incremented according to semantic versioning rules. Use this file as the single source of truth for project governance.

**Version**: 1.0.0 | **Ratified**: TODO(RATIFICATION_DATE): original adoption date unknown | **Last Amended**: 2025-10-07
