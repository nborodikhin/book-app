## Why

Readers need a lightweight, offline-friendly way to discover books via OpenLibrary and maintain a personal reading list with notes, without creating an account or depending on a proprietary backend.

## What Changes

- Introduce a two-tab Android app (Search + Bookmarks) backed by the public OpenLibrary API
- Add local persistence for bookmarked books (Room DB) and user preferences (DataStore)
- Implement in-memory caching of search results for the app lifecycle
- Support paginated search with debounce, book detail view, and per-book notes

## Capabilities

### New Capabilities

- `book-search`: Text-based search (title + author) against OpenLibrary API with debounce, pagination, in-memory cache, loading/error states, and a bookmark toggle per result
- `bookmarks`: Local list of bookmarked books persisted in Room DB; bookmark state and per-book notes stored in DataStore; data fetched from OpenLibrary once on first bookmark and never deleted
- `book-detail`: Full-screen detail view showing cover image (Coil), author, title, synopsis, and an editable note field (only for bookmarked books)

### Modified Capabilities

## Impact

- **New dependencies**: Room DB, DataStore, Coil, Retrofit (or Ktor) for OpenLibrary HTTP calls, kotlinx.serialization or Gson
- **Navigation**: Compose Navigation with a bottom nav bar (two tabs) and a detail screen on top
- **Network**: OpenLibrary Search API (`/search.json`), Works API (`/works/{id}.json`), Covers API
- **Storage**: Room entity for bookmarked book data; DataStore for bookmark flags and notes
- **No backend**: Entirely client-side; no auth, no server
