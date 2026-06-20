## Why

When a search query returns zero results the Search tab shows a blank screen, giving users no feedback. The Bookmarks tab already handles its two empty states correctly ("No bookmarks yet" / "No results for..."); this change closes the equivalent gap on the Search tab.

## What Changes

- Add a "No results" empty-state message to the Search tab when a search completes successfully but returns zero results
- No changes to the Bookmarks tab (already implemented and spec'd)

## Capabilities

### New Capabilities

(none)

### Modified Capabilities

- `book-search`: Add a new requirement for the empty-results state — when a search succeeds but the API returns no matching books, the app SHALL display a "No results" message instead of a blank list.

## Impact

- `SearchScreen.kt`: `Success` branch in `SearchScreenContent` gains an empty-state check
- `SearchViewModel.kt`: no changes needed — `Success` with an empty list is already the correct state
- `SearchScreenTest.kt`: new test for the empty-results UI
- `SearchViewModelTest.kt`: no new tests needed (state model unchanged)
