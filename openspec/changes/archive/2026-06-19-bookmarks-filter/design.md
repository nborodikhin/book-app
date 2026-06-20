## Context

The Bookmarks screen is a simple `LazyColumn` driven by `BookmarksViewModel.bookmarkedBooks: StateFlow<List<BookEntity>>`, which streams all bookmarked books from Room via `BookRepository.getBookmarkedBooks()`. There is no client-side filtering today. The filter must be purely in-memory — no DB query changes, no debounce, no network involvement.

## Goals / Non-Goals

**Goals:**
- Add a `TextField` filter input at the top of the Bookmarks screen
- Filter the visible list immediately as the user types, matching title or author (case-insensitive substring)
- Show a "No results" empty state when the query matches nothing but bookmarks exist
- Keep the existing "No bookmarks yet" empty state when the user has no bookmarks at all

**Non-Goals:**
- Debounce / delayed filtering (instant update is the requirement)
- Persisting the filter query across navigation or app restarts
- Filtering on fields other than title and author
- Changes to the Room query or repository layer

## Decisions

### Filter state lives in the ViewModel

`filterQuery: MutableStateFlow<String>` is added to `BookmarksViewModel`. The filtered list is derived with `combine(bookmarkedBooks, filterQuery)` — a `StateFlow<List<BookEntity>>` called `filteredBooks`.

**Why not filter in the composable?** Keeping derived state in the VM means the filter survives recomposition and is easily testable without UI.

**Alternative — filter inline in the composable**: simpler, but loses testability and breaks the pattern already established in this codebase (SearchViewModel follows the same approach).

### No debounce

The list is filtered in-memory with a simple `filter { }` call. For any realistic bookmarks count this is instantaneous; adding a delay would only make the UX feel sluggish.

### Separate `filteredBooks` StateFlow instead of mutating `bookmarkedBooks`

`bookmarkedBooks` stays as-is so that toggle operations (unbookmark) continue working correctly. `filteredBooks` is the output consumed by the UI.

### Empty-state distinction by query

`BookmarksScreenContent` receives both `allEmpty: Boolean` (no bookmarks at all) and the filtered list. When `filteredBooks` is empty but `allEmpty` is false, show "No results for '<query>'" instead of "No bookmarks yet".

## Risks / Trade-offs

- [Large bookmarks list] For thousands of entries the `combine + filter` will block the coroutine briefly. → Acceptable; realistic users have tens to low-hundreds of bookmarks. No mitigation needed.
- [Empty-state logic] Two empty states add a small bit of complexity to `BookmarksScreenContent`. → Mitigated by passing a simple Boolean flag rather than raw counts.
