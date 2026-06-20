## 1. ViewModel

- [x] 1.1 Add `onRetry: () -> Unit` lambda to `SearchUiState.Error` and pass `::retrySearch` when emitting that state in `fetchPage`
- [x] 1.2 Add `retrySearch()` method that calls `resetAndSearch(searchQuery.value)` if the query is not blank
- [x] 1.3 Add `paginationError: Boolean = false` field to `SearchUiState.Success` and emit it with `paginationError = true` when a page > 1 fetch fails (keep existing results, do not transition to Error)

## 2. UI — SearchScreen

- [x] 2.1 Add a `Button("Retry")` to the `SearchUiState.Error` branch in `SearchScreenContent`, wired to `state.onRetry`
- [x] 2.2 Add an inline error row at the bottom of the `LazyColumn` in the `Success` branch: shown when `state.paginationError == true`, displays "Failed to load more." text and a "Retry" `TextButton`; tapping it calls `onRetryPagination`
- [x] 2.3 Add `onRetryPagination: () -> Unit` parameter to `SearchScreenContent` (default `{}`) and wire it to `viewModel::loadNextPage` in `SearchScreen`
- [x] 2.4 Update `SearchScreen` to pass `onRetryPagination = { viewModel.loadNextPage() }`

## 3. Previews

- [x] 3.1 Update `SearchErrorPreview` to pass a no-op lambda for `onRetry`
- [x] 3.2 Add `SearchPaginationErrorPreview` showing `Success` state with `paginationError = true` and some fake results

## 4. Tests

- [x] 4.1 `SearchViewModelTest`: add test — when `fetchPage(query, 1)` throws, `uiState` is `Error` with a non-null `onRetry`
- [x] 4.2 `SearchViewModelTest`: add test — calling `onRetry` (or `retrySearch()`) transitions state back to `Loading` then `Success`
- [x] 4.3 `SearchViewModelTest`: add test — when `fetchPage(query, 2+)` throws, `uiState` remains `Success` with `paginationError = true` and previous results intact
- [x] 4.4 `SearchViewModelTest`: add test — calling `loadNextPage()` again after a pagination error clears `paginationError` and sets `isLoadingMore = true`
- [x] 4.5 `SearchScreenTest`: add test — `Error` state renders a node with text "Retry" that can be clicked
- [x] 4.6 `SearchScreenTest`: add test — `Success` state with `paginationError = true` renders inline "Failed to load more." text and a clickable "Retry" button
