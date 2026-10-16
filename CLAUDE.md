# Project Guidelines — Java / Spring Boot

## Technology Stack

- Java 21
- Spring Boot; use the version defined in `pom.xml`
- Maven 3.9.x; use the Maven Wrapper when available
- JUnit 5, Mockito, Spring Test
- Windows development environment; IntelliJ IDEA
- Use existing dependencies and project conventions before introducing alternatives

## Development Workflow

- Inspect requirements, existing code, dependencies, and tests before proposing changes.
- Use `/analiza` for read-only analysis when invoked.
- Use Claude Code Plan Mode for nontrivial changes. Keep plans short and actionable.
- Identify requirements, assumptions, affected files, implementation steps, and verification.
- Separate essential work from optional improvements.
- Clarify ambiguities that affect correctness. State reasonable assumptions for minor details.
- Obtain explicit approval before modifying files. Keep each file edit subject to manual approval.
- Implement in small, verifiable increments.
- Prefer working functionality over speculative abstractions.
- Use English for code, comments, documentation, plans, and technical communication.

## Time-Boxed Delivery

- For a 60-minute task, prioritize essential, working functionality.
- Keep planning proportional to complexity. Implement in small, testable steps.
- Reserve time for build verification, tests, and a final review.
- Defer optional features, dependencies, and cosmetic improvements.

## File and Git Safety

- Never execute Git commands that change repository state.
- Permitted read-only commands: `git status`, `git diff`, `git log`.
- Do not stage, commit, push, switch branches, reset, rebase, merge, or stash.
- Do not delete or rename files, install global tools, modify system settings, or access external services without approval.
- Never expose credentials, tokens, or secrets.
- Use Windows CMD-compatible commands unless the active shell is different.
- Avoid reserved Windows filenames (`nul`, `con`, `prn`, `aux`, `com1`–`com9`, `lpt1`–`lpt9`).

## Java Conventions

- Use descriptive names and cohesive methods.
- Prefer clear control flow, early returns, and limited nesting.
- Apply SOLID and DRY pragmatically; avoid unnecessary abstraction.
- Prefer immutable values and Java `record` for suitable DTOs.
- Use `java.time` for date and time values.
- Use `BigDecimal` for monetary calculations where precision matters.
- Use enums for well-defined, finite sets of values.
- Prefer constructor injection; use Lombok `@RequiredArgsConstructor` when available.
- Avoid field injection with `@Autowired`.
- Keep configuration outside source code. Never hardcode secrets or environment-specific URLs.
- Write comments to explain intent or non-obvious decisions.
- Remove unused code, imports, and dependencies.

## Backend Structure

Preferred package layout for layered applications:

```text
src/main/java/<base-package>/
  config/
  controller/
  domain/
    dto/
    mapper/
  exception/
  persistence/
    model/
    repository/
  service/
    impl/
```

- **Controller:** HTTP handling, request validation, response mapping.
- **Service:** business rules, orchestration, transaction boundaries.
- **Repository:** persistence queries and data access.
- **DTO:** API input/output contracts; keep separate from persistence entities.
- **Mapper:** entity/DTO conversion when mapping warrants separation.
- **Config:** application configuration, security, integrations.
- **Exception:** domain exceptions and consistent API error handling.

Adapt the structure to actual complexity. Do not create empty layers or packages.

## Architecture Patterns

- **Controller → Service → Repository:** preferred for applications with business logic and persistence.
- **Service interface → ServiceImpl:** use for multiple implementations, meaningful contracts, or useful abstraction. A concrete service is valid for simple cases.
- **DTO / Mapper:** protect API contracts from persistence details. Use manual mapping for small models; consider MapStruct for substantial mapping.
- **Spring Data JPA:** use entities, repositories, relationships, and database constraints when persistence is required.
- **Transactions:** place `@Transactional` at service boundaries. Use `readOnly = true` for appropriate reads. Account for proxies, rollback behavior, and transaction duration.
- **Validation:** use Bean Validation (`@Valid`, `@NotNull`, `@NotBlank`, `@Min`, etc.) and enforce domain invariants.
- **Exception handling:** use specific exceptions and appropriate HTTP statuses. Introduce `@RestControllerAdvice` and `ApiError` or `ProblemDetail` when consistent error contracts are needed.
- **Configuration:** use `application.properties`/YAML and `@ConfigurationProperties` where useful.
- **Database migrations:** use Liquibase for managed schema evolution when appropriate.
- **Security:** use Spring Security, JWT, OAuth2, and role-based authorization when required.
- **API documentation:** use springdoc OpenAPI / Swagger when API exploration or documentation is relevant.
- **Observability:** use Spring Boot Actuator and metrics when operational requirements justify them.
- **Infrastructure:** consider H2, PostgreSQL, Docker Compose, caching, async processing, and messaging according to requirements.

These are available architectural options, not mandatory dependencies. Select and explain relevant patterns before implementation.

## REST API Guidelines

- Follow the required endpoint paths, payloads, status codes, and data formats.
- Keep controllers thin; avoid business logic in HTTP handlers.
- Use DTOs for nontrivial request and response models.
- Use `ResponseEntity` when status or header control is needed; direct return values are valid for simple responses.
- Define date serialization explicitly when an API contract requires a particular format.
- Validate input and return safe, consistent errors.
- Do not expose stack traces or internal exception messages.
- Implement sorting, filtering, pagination, and stable ordering when relevant.
- Avoid redundant CORS configuration when a development proxy already handles requests.

## Data and Reliability

- Validate input formats, boundary values, duplicates, and missing data.
- Use robust parsing for structured formats; account for quoting, escaping, and malformed records.
- Handle startup and import failures explicitly; do not silently leave inconsistent state.
- Consider idempotency, concurrent updates, locking, and retry behavior when applicable.
- Keep external calls outside long-running database transactions where possible.
- Avoid unnecessary queries, N+1 loading, and unbounded in-memory processing.

## Testing and Verification

- Use JUnit 5 for business logic and edge cases.
- Use Mockito for isolated unit tests when dependencies need mocking.
- Use MockMvc / `@WebMvcTest` for controller behavior and HTTP contracts.
- Use `@SpringBootTest` for full application context or integration tests when needed.
- Do not treat a web-slice test as proof that the full application starts.
- Cover core behavior, relevant edge cases, and failure paths.
- Fix or remove outdated generated tests; do not disable failing tests without explanation.
- Run the smallest useful verification after each meaningful change.

Typical commands:

```cmd
mvn clean test
mvn spring-boot:run
```

Use `mvnw.cmd` when the project includes the Maven Wrapper. Report only checks actually executed, including failures and remaining gaps.

## Session Notes

- Read `.claude/session.md` at session start if it exists.
- Keep notes concise: current objective, completed work, decisions, verification, next steps.
- Propose updates after meaningful milestones; obtain approval before writing.
- Keep `.claude/session.md` local and excluded from Git.
- Do not create duplicate progress files or additional memory structures by default.

## Working Principles

- Correctness first; maintainability second; optional polish last.
- Preserve useful architectural conventions without imposing unnecessary layers.
- Prefer focused changes and explicit trade-offs.
- Verify behavior before declaring work complete.
