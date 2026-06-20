## Why

User-visible strings are hardcoded inline throughout the Compose UI, making them impossible to localize and hard to keep consistent. Moving them into `strings.xml` establishes the foundation for i18n and ensures a single source of truth for all copy.

## What Changes

- All user-visible hardcoded strings in Kotlin UI files replaced with `stringResource()` calls referencing `strings.xml` entries
- `strings.xml` populated with entries for labels, placeholders, error messages, content descriptions, and empty-state messages across Search, Bookmarks, BookDetail, BookListItem, and Navigation screens
- No behavioral changes — purely a structural refactor

## Capabilities

### New Capabilities

- `string-resources`: Centralised Android string resource definitions covering all user-visible copy in the app

### Modified Capabilities

<!-- None — no spec-level behavior changes, this is an implementation-only refactor -->

## Impact

- `app/src/main/res/values/strings.xml` — new string entries added
- `app/src/main/java/com/example/bookapp/ui/navigation/BookshelfApp.kt` — tab labels
- `app/src/main/java/com/example/bookapp/ui/search/SearchScreen.kt` — placeholder, loading/empty/error states, retry button
- `app/src/main/java/com/example/bookapp/ui/search/SearchViewModel.kt` — error message string
- `app/src/main/java/com/example/bookapp/ui/bookmarks/BookmarksScreen.kt` — filter placeholder, empty-state messages
- `app/src/main/java/com/example/bookapp/ui/detail/BookDetailScreen.kt` — screen title, back button, synopsis fallback, note label, bookmark content descriptions
- `app/src/main/java/com/example/bookapp/ui/components/BookListItem.kt` — cover/bookmark content descriptions
- `app/src/main/java/com/example/bookapp/ui/detail/BookDetailViewModel.kt` — error message string
- No external API or dependency changes
