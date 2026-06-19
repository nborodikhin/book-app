## Context

A fresh Android app (Jetpack Compose, minSdk 24) with no existing source. The app is a pure client — no backend, no auth. All data comes from the public OpenLibrary REST API or local storage. The project uses AGP 9 and Kotlin.

## Goals / Non-Goals

**Goals:**
- Two-tab UI: Search (live, paginated, debounced) and Bookmarks (offline-first)
- Book detail screen reachable from both tabs
- Bookmark state and per-book notes persisted across sessions
- Bookmarked book data cached locally forever (never deleted)
- Search results cached in memory for the app lifecycle
- Works entirely offline for bookmarked content

**Non-Goals:**
- User accounts or sync across devices
- Editing or contributing to OpenLibrary
- Reading/ebook functionality
- Notifications or background sync

## Decisions

### 1. Architecture: MVVM + Repository pattern
Each feature (search, bookmarks, book-detail) has its own `ViewModel` and a shared `BookRepository`. The repository is the single source of truth, routing reads to Room or the network as needed.

**Alternative considered**: MVI — adds complexity not justified for this app size.

### 2. Navigation: Compose Navigation with a single `NavHost`
Bottom nav bar hosts Search and Bookmarks tabs. Book detail is a full-screen destination pushed on top (`/book/{workId}`). Both tabs can navigate to detail.

### 3. Network: Retrofit + kotlinx.serialization
OpenLibrary endpoints used:
- Search: `GET https://openlibrary.org/search.json?q={query}&fields=key,title,author_name,cover_i&page={page}&limit=20`
- Work detail: `GET https://openlibrary.org/works/{id}.json`
- Cover image: `https://covers.openlibrary.org/b/id/{cover_id}-M.jpg` (loaded by Coil, not fetched manually)

**Alternative considered**: Ktor — Retrofit is simpler for a REST-only client.

### 4. Search caching: in-memory `Map<query+page, List<SearchResult>>` in the repository
Keyed by `"$query:$page"`. Lives for the app process lifetime. No disk persistence needed.

### 5. Pagination: manual page counter in `SearchViewModel`
`LazyColumn` + `LazyListState` detects when the user scrolls near the end and triggers the next page load. No Paging 3 library — keeps it simple.

**Alternative considered**: Paging 3 — overkill given the simple cache strategy and manual page tracking already needed.

### 6. Bookmark persistence: Room for book data, DataStore for flags + notes
- Room `BookEntity`: workId, title, authors, synopsis, coverUrl — written once on first bookmark, never updated or deleted
- DataStore (Preferences): `bookmarked_<workId>` (Boolean), `note_<workId>` (String)
- Rationale: book data is immutable reference data (Room fits); flags and notes are small key-value pairs (DataStore fits, avoids Room migration churn for simple prefs)

### 7. Image loading: Coil
`AsyncImage` composable with placeholder and error drawables. Cover URLs constructed from `cover_id` returned by search results.

### 8. Debounce: 2-second delay via `StateFlow` + `debounce` operator in `SearchViewModel`
```
searchQuery
  .debounce(2000)
  .distinctUntilChanged()
  .collect { query -> search(query, page = 1) }
```

### 9. Dependency injection: Hilt
Provides `BookRepository`, `OpenLibraryApi`, `BookDao`, `DataStore` singletons. Standard Hilt setup with `@HiltAndroidApp`, `@AndroidEntryPoint`.

## Risks / Trade-offs

- [OpenLibrary rate limits / availability] → In-memory cache reduces repeat calls; error state with retry prompt handles failures gracefully
- [DataStore key explosion for many bookmarks] → Acceptable at personal-library scale; a dedicated notes table in Room would be cleaner at scale
- [Work detail API latency on first bookmark] → Fetched eagerly on bookmark tap; show loading indicator in detail screen until data arrives
- [cover_id missing for some books] → Show placeholder image; `cover_id` is nullable in the data model
- [Synopsis absent for many works] → Show "No description available" fallback text
