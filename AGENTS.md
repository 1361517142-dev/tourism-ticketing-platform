# AGENTS.md

## Project

This repository is a modular monolith for a tourism ticketing platform built with Java 17, Spring Boot, Maven, MyBatis, and MySQL.

Priorities:

- Preserve business correctness, data consistency, and readability.
- Read relevant files under `docs/` before implementation.
- If code and documentation conflict, report the conflict instead of guessing.
- Make only the changes required for the current task.

## Repository Layout

```text
tourism-ticketing-platform/
├── pom.xml
├── docs/
├── tourism-common/    # Shared responses, exceptions, constants, utilities
├── tourism-pojo/      # Entities, DTOs, VOs, enums
└── tourism-server/    # Spring Boot application and business logic
```

Business packages in `tourism-server` include:

`auth`, `venue`, `session`, `tickettype`, `visitor`, `booking`, `ticket`, and `verification`.

Organize each package with `controller`, `service`, and `mapper` only when needed.

## Architecture Rules

- Controllers handle HTTP concerns, validation, and Service calls only.
- Services own business workflows and transactions.
- Mappers handle database access only.
- Modules collaborate through Services, never through another module's Mapper.
- Do not expose Entities directly through APIs; use DTOs for requests and VOs for responses.
- Every database schema change requires a new Flyway migration.
- Never edit or delete an applied versioned migration.

## Coding Rules

- Use constructor injection; do not use field injection.
- Use Jakarta Validation for request validation.
- Use centralized exception handling and consistent API responses.
- Use `BigDecimal` for money.
- Use `LocalDate`, `LocalTime`, and `LocalDateTime` for date/time values.
- Use enums or centralized constants for statuses; avoid magic strings and numbers.
- Keep methods focused and comment business reasons, not obvious code behavior.

## Editing and Testing

Before editing, inspect relevant code, tests, `pom.xml`, and documentation.

- Avoid unrelated refactoring, deletion, or formatting.
- Do not change existing APIs, schemas, statuses, or module boundaries without explaining the impact.
- Add or update tests for behavior changes.
- Update documentation for user-visible changes.
- Do not create branches, commit, or push unless explicitly requested.
- Never claim tests passed if they were not run.

Run from the repository root:

```bash
./mvnw clean test
./mvnw -pl tourism-server -am test
./mvnw -pl tourism-server -am spring-boot:run
```

Use `mvnw.cmd` on Windows, or `mvn` if the wrapper is unavailable.

After completing a task, report:

- changed files;
- key design decisions;
- tests executed;
- remaining risks or unverified items.

## Optional Skills

Load a skill only when the task matches and the file exists:

- Spring Boot: `tourism-server/.agents/skills/java-springboot/SKILL.md`
- JUnit/testing: `tourism-server/.agents/skills/java-junit/SKILL.md`
- Build/testing: `.agents/skills/building-and-testing/SKILL.md`

If no relevant skill exists, follow this file, project documentation, and existing code patterns.
