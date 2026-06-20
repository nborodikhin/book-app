## 1. Add string entries to strings.xml

- [x] 1.1 Add navigation tab label strings (`nav_tab_search`, `nav_tab_bookmarks`)
- [x] 1.2 Add search screen strings: placeholder, loading content description, no-results label, pagination error, retry button label, error icon content description
- [x] 1.3 Add bookmarks screen strings: filter placeholder, empty-no-bookmarks label, empty-no-results format string (`%s`)
- [x] 1.4 Add book detail screen strings: screen title, back button content description, synopsis fallback, note field label, bookmark content descriptions
- [x] 1.5 Add shared BookListItem strings: cover content description format string, bookmark/unbookmark content descriptions

## 2. Update ViewModels to use context.getString()

- [x] 2.1 Update `SearchViewModel` to accept `Application` context and replace inline error string with `getString(R.string.search_error_message)`
- [x] 2.2 Update `BookDetailViewModel` to accept `Application` context and replace inline error string with `getString(R.string.detail_error_message)`

## 3. Update Compose UI files to use stringResource()

- [x] 3.1 Update `BookshelfApp.kt` navigation tab labels to use `stringResource()`
- [x] 3.2 Update `SearchScreen.kt` to use `stringResource()` for all hardcoded strings
- [x] 3.3 Update `BookmarksScreen.kt` to use `stringResource()` for all hardcoded strings (including format-arg empty-state)
- [x] 3.4 Update `BookDetailScreen.kt` to use `stringResource()` for all hardcoded strings
- [x] 3.5 Update `BookListItem.kt` to use `stringResource()` for content descriptions (with format args for title)

## 4. Verify

- [x] 4.1 Run `grep -rn '"[A-Z]' app/src/main/java` and confirm no user-visible string literals remain outside preview data
- [x] 4.2 Build the app and confirm it compiles with no errors
- [x] 4.3 Run existing UI tests and confirm they pass
