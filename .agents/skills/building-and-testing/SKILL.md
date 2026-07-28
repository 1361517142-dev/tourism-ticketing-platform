---
name: building-and-testing
description: >
  How to build and test the project: Maven commands, module selection,
  targeted tests, packaging, and important build rules.
---

# Build System

The project uses Maven and Spring Boot. Build from the repository root and build selectively when possible.
Use the Maven Wrapper so every environment uses the repository's configured Maven version.

## Quick Build (First Time)

```bash
./mvnw clean install -DskipTests
```

This resolves dependencies, compiles all modules, packages them, and installs local module artifacts without running tests.

## Building a Specific Module

```bash
# A module and all modules it depends on
./mvnw install -pl tourism-server -am -DskipTests

# A single library module
./mvnw install -pl tourism-common -DskipTests
```

## Building the Application

```bash
./mvnw package -pl tourism-server -am -DskipTests
```

The packaged application is written to `tourism-server/target/`.

## Running Tests

```bash
# Run all tests
./mvnw test

# Run tests for the application module and required modules
./mvnw test -pl tourism-server -am

# Run a single test class
./mvnw test -f tourism-server/pom.xml -Dtest=MyTest

# Run a single test method
./mvnw test -f tourism-server/pom.xml -Dtest=fully.qualified.ClassName#methodName

# Run the full Maven verification lifecycle
./mvnw verify
```

## Incremental Build

```bash
# Build one changed module and its upstream and downstream modules
./mvnw install -pl <module> -am -amd -DskipTests
```

## Key Maven Flags

| Flag | Purpose |
|------|---------|
| `-pl <module>` | Select one or more reactor modules |
| `-am` | Also build modules required by the selected module |
| `-amd` | Also build modules that depend on the selected module |
| `-DskipTests` | Compile tests but skip running them |
| `-Dtest=...` | Run a specific test class or method |
| `-U` | Force Maven to check for updated dependencies |
| `-q` | Reduce Maven console output |

## Maven Wrapper

The wrapper files (`mvnw`, `mvnw.cmd`, and `.mvn/`) belong in the repository root.
Run `./mvnw` on Unix-like systems and `mvnw.cmd` on Windows. Use `./mvnw -v`
to inspect the Java and Maven versions selected by the wrapper.

## Important Build Rules

- Run reactor builds from the repository root
- Always `install` when another local module needs to consume changed artifacts
- Use `-pl` with `-am` instead of rebuilding unrelated modules
- Run targeted tests first, then run the full relevant module test suite
- Use `./mvnw` or `mvnw.cmd`, not an arbitrary system Maven installation
- After adding, removing, or renaming a module, update the root `pom.xml` and module parent references
