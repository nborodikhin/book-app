# Spec: Coroutine Exception Handling

## Purpose

Defines rules for safe exception handling inside Kotlin suspend functions, ensuring that `CancellationException` is never swallowed and that structured concurrency cancellation always propagates correctly.

## Requirements

### Requirement: Suspend functions must not swallow CancellationException
Every `catch` block that catches `Exception` (or any supertype that includes `CancellationException`) inside a suspend function SHALL rethrow `CancellationException` before executing any other handler logic. This applies to all production suspend functions in the app.

#### Scenario: Cancelled coroutine propagates cancellation through repository
- **WHEN** a coroutine calling `BookRepository.getBookDetail()` is cancelled while the network request is in flight
- **THEN** `CancellationException` propagates out of `getBookDetail()` and the coroutine terminates without returning a value or setting UI state

#### Scenario: Cancelled coroutine propagates cancellation through ViewModel fetch
- **WHEN** a coroutine calling `SearchViewModel.fetchPage()` is cancelled
- **THEN** `CancellationException` propagates out of `fetchPage()` and the coroutine terminates without setting an error UI state

#### Scenario: Genuine network error still handled correctly
- **WHEN** `repository.search()` throws a non-cancellation exception
- **THEN** the catch block handles it normally (error state or fallback), unchanged from current behaviour

### Requirement: CancellationException rethrow pattern documented in CLAUDE.md
`CLAUDE.md` SHALL include a Kotlin Coroutines section stating the rule and showing the correct code pattern, so that future contributors apply it consistently.

#### Scenario: CLAUDE.md contains the rethrow pattern
- **WHEN** a developer reads CLAUDE.md before writing a new suspend function with error handling
- **THEN** they find explicit guidance to add `catch (e: CancellationException) { throw e }` before any `catch (e: Exception)` block

### Requirement: Coroutine cancellation safety is enforced by tests and lint
Each `suspend fun` that uses `try/catch (e: Exception)` SHALL have a corresponding unit test verifying that legitimate errors are caught while normal flow (non-exception path) completes correctly. Additionally, the lint baseline SHALL not suppress `kotlin-coroutines` related lint warnings, so that new violations are flagged automatically.

#### Scenario: Error path test exists for each guarded suspend function
- **WHEN** the unit test suite is run
- **THEN** at least one test exercises the error branch of each `suspend fun` that has a `CancellationException` rethrow guard

#### Scenario: Lint does not suppress coroutine-related warnings
- **WHEN** `./gradlew lint` is run
- **THEN** no coroutine-related lint issues are suppressed in the baseline that are not already suppressed with an inline `@SuppressLint` and a justification comment
