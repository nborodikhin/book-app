## Why

The Bookmarks tab shows all saved books in a flat list with no way to narrow it down. Users with many bookmarks have to scroll to find a specific title or author — a search/filter field fixes this.

## What Changes

- A filter text field is added at the top of the Bookmarks screen
- As the user types, the visible list is immediately filtered to books whose title or author name contains the query (case-insensitive)
- When the field is empty the full list is shown
- No debounce delay — filtering is instant

## Capabilities

### New Capabilities
- `bookmarks-filter`: Real-time text filter on the Bookmarks screen that narrows the displayed list by title and author

### Modified Capabilities
- `bookmarks`: The Bookmarks tab gains a filter input above the list; the "empty state" message must distinguish between "no bookmarks at all" and "no matches for the current query"

## Impact

- `BookmarksScreen` / `BookmarksViewModel` — add filter state and filtered-list derivation
- No new dependencies, no network calls, no DB changes
