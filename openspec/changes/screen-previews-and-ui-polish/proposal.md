## Why

The UI has no `@Preview` composables, making iterative design slow and requiring a full build to inspect layouts. Two existing behaviors also need correction: the note text field re-reads from the ViewModel on every recomposition (losing cursor position and causing flicker), and the bookmark icon scrolls away with the book cover instead of staying pinned at the top of the screen.

## What Changes

- Add `@Preview`-annotated composables for all three screens (`SearchScreen`, `BookmarksScreen`, `BookDetailScreen`) and the shared `BookListItem` component, covering key states (empty, loading, success, error, bookmarked/unbookmarked)
- Refactor the note `OutlinedTextField` in `BookDetailScreen` so the screen holds the authoritative text state via `remember { mutableStateOf(...) }`, initialized once from the ViewModel's `note` flow; subsequent keystrokes update local state and notify the VM but do not re-initialize from it
- Move the bookmark `IconButton` out of the scrollable `Column` / cover `Box` and into a static overlay anchored to the top-end corner of the detail screen's content area, so it remains visible regardless of scroll position
- Replace the shallow `BookDetailScreenTest`, `SearchScreenTest`, and `BookmarksScreenTest` instrumented tests with proper composable-level tests that call the stateless overloads (the same ones used by `@Preview`) with deterministic state, covering all spec scenarios including note field visibility, character counter, error/empty states, and bookmark icon state

## Capabilities

### New Capabilities
- `compose-previews`: `@Preview` functions covering all screens and the `BookListItem` component across their major UI states
- `note-edit-state`: Local `remember`-based state for the note text field, initialized once from the VM and independently maintained by the screen thereafter
- `detail-bookmark-anchor`: Bookmark icon pinned to a static top-end position outside the scrollable content on the book detail screen
- `stateless-screen-tests`: Composable-level instrumented tests for `BookDetailScreen`, `SearchScreen`, and `BookmarksScreen` that call stateless overloads with injected state, replacing the current shallow stub tests and covering all spec scenarios

### Modified Capabilities
<!-- none — specs directory is empty; all capabilities are new -->

## Impact

- `BookDetailScreen.kt` — refactored for local note state and repositioned bookmark icon
- `SearchScreen.kt`, `BookmarksScreen.kt`, `BookListItem.kt` — preview functions added (same files or companion preview files)
- `BookDetailViewModel.kt` — `onNoteChange` signature stays the same; VM continues to receive updates but no longer drives recomposition of the text field value after initialization
- `BookDetailScreenTest.kt`, `SearchScreenTest.kt`, `BookmarksScreenTest.kt` — replaced with proper composable-level tests against the stateless overloads
- No new dependencies; `@Preview` is part of the existing `compose-ui-tooling` dep
