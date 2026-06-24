## ADDED Requirements

### Requirement: Coroutine cancellation safety is enforced by tests and lint
Each `suspend fun` that uses `try/catch (e: Exception)` SHALL have a corresponding unit test verifying that legitimate errors are caught while normal flow (non-exception path) completes correctly. Additionally, the lint baseline SHALL not suppress `kotlin-coroutines` related lint warnings, so that new violations are flagged automatically.

#### Scenario: Error path test exists for each guarded suspend function
- **WHEN** the unit test suite is run
- **THEN** at least one test exercises the error branch of each `suspend fun` that has a `CancellationException` rethrow guard

#### Scenario: Lint does not suppress coroutine-related warnings
- **WHEN** `./gradlew lint` is run
- **THEN** no coroutine-related lint issues are suppressed in the baseline that are not already suppressed with an inline `@SuppressLint` and a justification comment
