## Context

The Search tab uses a sealed `SearchUiState` with four states: `Idle`, `Loading`, `Success`, `Error`. The current `Error` state is used for both initial-search failures and pagination failures. When a pagination failure occurs, `fetchPage` replaces the `Success` state with `Error`, discarding the already-loaded results. There is no retry affordance in either case.

## Goals / Non-Goals

**Goals:**
- Let users retry a failed initial search without re-typing the query
- Let users retry a failed pagination request while keeping the results already on screen
- Keep the two error surfaces visually distinct (full-screen vs. inline at bottom of list)

**Non-Goals:**
- Automatic/silent retry on failure
- Distinguishing error types (timeout, no connection, 5xx) in the UI
- Retry for non-search operations (bookmarking, book detail)

## Decisions

### 1. Pagination error as a field on `Success`, not a separate state

**Decision:** Add an optional `paginationError: Boolean` flag to `SearchUiState.Success` rather than introducing a new `PaginationError` state.

**Rationale:** The results list is still valid and should remain visible. A separate state would force the composable to branch on a new case and duplicate the list-rendering logic. The flag is a thin annotation on an already-stable state.

**Alternative considered:** `SearchUiState.PaginationError(results, query)` — rejected because it duplicates the Success rendering path.

### 2. Retry callbacks passed through `SearchUiState`, not the ViewModel reference

**Decision:** `SearchUiState.Error` carries an `onRetry: () -> Unit` lambda. `SearchUiState.Success` with `paginationError = true` relies on a separate `onRetryPagination: () -> Unit` callback passed into the composable alongside `uiState`.

**Rationale:** Composables should not depend directly on the ViewModel type; lambdas keep `SearchScreenContent` testable in isolation. The Error state already carries display data, so bundling retry there is consistent.

**Alternative considered:** Pass `viewModel` directly into the composable — rejected; breaks preview and unit testability.

### 3. ViewModel retry methods

`retrySearch()` re-runs `resetAndSearch(searchQuery.value)`. For pagination, `loadNextPage()` already has the correct logic; the composable calls it again via the `onRetryPagination` lambda — no new method needed.

## Risks / Trade-offs

- **Stale query on retry** — `retrySearch()` reads `searchQuery.value` at call time. If the field is cleared between error and tap, retry does nothing (returns Idle). This is acceptable; the user can see the empty field. Mitigation: none needed.
- **Lambda in state object** — `SearchUiState.Error(onRetry)` means two `Error` instances with different lambdas are not equal. This is fine; `StateFlow` emits on reference change anyway.
