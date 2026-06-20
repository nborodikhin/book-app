## 1. ViewModel

- [x] 1.1 Add `filterQuery: MutableStateFlow<String>` to `BookmarksViewModel`
- [x] 1.2 Add `updateFilter(query: String)` function that updates `filterQuery`
- [x] 1.3 Derive `filteredBooks: StateFlow<List<BookEntity>>` using `combine(bookmarkedBooks, filterQuery)` with case-insensitive title/author substring match

## 2. UI

- [x] 2.1 Add filter `TextField` above the list in `BookmarksScreenContent`, wired to a `filterQuery: String` parameter and `onFilterChange: (String) -> Unit` callback
- [x] 2.2 Pass `filterQuery` and `onFilterChange` from `BookmarksScreen` to `BookmarksScreenContent`, collecting from `viewModel.filterQuery` and calling `viewModel.updateFilter`
- [x] 2.3 Replace `bookmarkedBooks` with `filteredBooks` as the list source in `BookmarksScreen`
- [x] 2.4 Update empty-state logic in `BookmarksScreenContent`: show "No bookmarks yet" when all bookmarks are empty, show "No results for '<query>'" when filter matches nothing but bookmarks exist

## 3. Tests

- [x] 3.1 Unit test `BookmarksViewModel`: verify `filteredBooks` returns all books when query is empty, filters by title, filters by author, and is case-insensitive
- [x] 3.2 Update `BookmarksScreenTest` (instrumented): add test for filter field presence and verify list narrows when text is entered
