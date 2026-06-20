## Why

The Search tab currently shows a generic error message when a network failure occurs, with no way to retry without re-typing the query. This forces users to repeat work and makes the app feel fragile on flaky connections.

## What Changes

- Add a **Retry** button to the full-screen error state shown when an initial search fails
- Add an **inline error banner with a Retry button** at the bottom of the results list when a pagination request fails (preserving existing results instead of replacing them)
- ViewModel exposes retry actions for both error types

## Capabilities

### New Capabilities
- (none — this is a behaviour change to an existing capability)

### Modified Capabilities
- `book-search`: Error-state requirements change — the spec currently only requires an error icon + message; it must now also require a retry action for both initial-search and pagination errors, and must distinguish between the two error surfaces.

## Impact

- `SearchUiState` sealed interface: `Error` gains a `onRetry` callback; `Success` gains an optional `paginationError` field
- `SearchViewModel`: new `retrySearch()` and pagination retry path
- `SearchScreen` / `SearchScreenContent`: error UI updated with `Button`; pagination error rendered at bottom of `LazyColumn`
- `SearchScreenTest` / `SearchViewModelTest`: new test cases for retry flows
