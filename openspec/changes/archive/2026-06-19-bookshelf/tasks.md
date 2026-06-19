## 1. Project Setup & Dependencies

- [x] 1.1 Add dependencies to `app/build.gradle.kts`: Hilt, Room, DataStore, Retrofit, kotlinx.serialization, Coil, Compose Navigation
- [x] 1.2 Add Hilt plugin to `build.gradle.kts` and `settings.gradle.kts`
- [x] 1.3 Add KSP plugin for Room and Hilt annotation processing
- [x] 1.4 Annotate `Application` class with `@HiltAndroidApp`
- [x] 1.5 Add test dependencies: `kotlinx-coroutines-test`, `mockito-kotlin`, `turbine`, `robolectric` (unit); `androidx.compose.ui:ui-test-junit4`, `room-testing`, `androidx.compose.ui:ui-test-manifest` (instrumentation)

## 2. Data Layer — Network

- [x] 2.1 Create `OpenLibraryApi` Retrofit interface with `searchBooks(query, page, limit)` and `getWork(workId)` endpoints
- [x] 2.2 Create `SearchResponse` and `WorkDetailResponse` data classes (kotlinx.serialization)
- [x] 2.3 Create Hilt `NetworkModule` providing `Retrofit` and `OpenLibraryApi` singletons

## 3. Data Layer — Local Storage

- [x] 3.1 Create `BookEntity` Room entity (workId, title, authors, synopsis, coverUrl)
- [x] 3.2 Create `BookDao` with insert and query methods
- [x] 3.3 Create `AppDatabase` Room database class
- [x] 3.4 Create Hilt `DatabaseModule` providing `AppDatabase` and `BookDao`
- [x] 3.5 Create Hilt `DataStoreModule` providing `DataStore<Preferences>` singleton

## 4. Repository

- [x] 4.1 Create `BookRepository` with in-memory search cache (`Map<String, List<SearchResult>>`)
- [x] 4.2 Implement `search(query, page)` — checks cache first, falls back to network
- [x] 4.3 Implement `getBookDetail(workId)` — checks Room first, falls back to network
- [x] 4.4 Implement `setBookmarked(workId, bookmarked)` — updates DataStore; fetches and stores work detail in Room on first bookmark
- [x] 4.5 Implement `isBookmarked(workId): Flow<Boolean>` from DataStore
- [x] 4.6 Implement `getNote(workId): Flow<String>` and `setNote(workId, note)` via DataStore
- [x] 4.7 Implement `getBookmarkedBooks(): Flow<List<BookEntity>>` from Room

## 5. Navigation & App Shell

- [x] 5.1 Create `MainActivity` with `@AndroidEntryPoint` and set Compose content
- [x] 5.2 Create `BookshelfApp` composable with `NavHost` and bottom navigation bar (Search, Bookmarks tabs)
- [x] 5.3 Define nav routes: `search`, `bookmarks`, `book/{workId}`

## 6. Search Feature

- [x] 6.1 Create `SearchViewModel` with `searchQuery` StateFlow and 2-second debounce trigger
- [x] 6.2 Implement pagination logic in `SearchViewModel` (page counter, append results, detect end of list)
- [x] 6.3 Create `SearchScreen` composable with text input, `LazyColumn` of results, loading spinner, and error state
- [x] 6.4 Create `BookListItem` composable (cover image via Coil, author, title, bookmark icon toggle)
- [x] 6.5 Wire scroll-near-end detection in `SearchScreen` to trigger next page load
- [x] 6.6 Wire bookmark toggle in `SearchScreen` to `BookRepository.setBookmarked`

## 7. Bookmarks Feature

- [x] 7.1 Create `BookmarksViewModel` exposing `bookmarkedBooks: Flow<List<BookEntity>>`
- [x] 7.2 Create `BookmarksScreen` composable with `LazyColumn` of bookmarked books and empty state
- [x] 7.3 Reuse `BookListItem` composable for bookmark list items

## 8. Book Detail Feature

- [x] 8.1 Create `BookDetailViewModel` accepting `workId`; loads local data first, fetches from network if absent
- [x] 8.2 Create `BookDetailScreen` composable: cover with bookmark icon overlay, author, title, synopsis, note field
- [x] 8.3 Show note field only when book is bookmarked; enforce 300-char max; display live character counter ("X/300"); auto-save note on change via DataStore
- [x] 8.4 Wire bookmark toggle on detail screen to `BookRepository.setBookmarked`
- [x] 8.5 Show loading spinner while detail data is being fetched; show fallbacks for missing synopsis/cover

## 9. Polish & Error Handling

- [x] 9.1 Add placeholder and error drawables for Coil image loading
- [x] 9.2 Verify error state UI in Search (error icon + retry message)
- [x] 9.3 Verify empty state UI in Bookmarks tab
- [x] 9.4 Verify note field appears/disappears correctly when toggling bookmark on detail screen
- [x] 9.5 Write README.md with project description, tech stack, and OpenLibrary API usage

## 10. Unit Tests (JVM / Robolectric)

- [x] 10.1 `BookRepositoryTest`: verify search cache hit skips network call; cache miss calls API and stores result
- [x] 10.2 `BookRepositoryTest`: verify `setBookmarked(true)` on a new book fetches work detail and inserts into Room; second call skips fetch
- [x] 10.3 `BookRepositoryTest`: verify `setBookmarked(false)` clears bookmark flag in DataStore but does not delete Room entity
- [x] 10.4 `BookRepositoryTest`: verify `getNote` / `setNote` round-trip through DataStore; verify note cleared on unbookmark
- [x] 10.5 `SearchViewModelTest`: verify debounce — rapid input changes produce only one search call after 2 s idle (use `advanceTimeBy` + `StandardTestDispatcher`)
- [x] 10.6 `SearchViewModelTest`: verify pagination — scrolling near end appends next page; no request sent when last page already loaded
- [x] 10.7 `SearchViewModelTest`: verify loading state emitted during fetch; error state emitted on network failure
- [x] 10.8 `BookDetailViewModelTest`: verify local-first — bookmarked book data served from Room without network call
- [x] 10.9 `BookDetailViewModelTest`: verify non-local book triggers network fetch and shows loading state
- [x] 10.10 `BookDetailViewModelTest`: verify note capped at 300 chars — input beyond limit is truncated/ignored

## 11. Instrumentation Tests (on-device)

- [x] 11.1 `BookDaoTest`: insert a `BookEntity` and verify it is retrieved correctly; verify entity is not deleted after unbookmark (no delete DAO method)
- [x] 11.2 `BookDaoTest`: verify `getBookmarkedBooks()` Flow emits updated list when new entities are inserted — use in-memory `Room.inMemoryDatabaseBuilder`
- [x] 11.3 `SearchScreenTest`: verify typing in search field shows results list after debounce; verify spinner shown during load; verify error state shown on failure
- [x] 11.4 `SearchScreenTest`: verify tapping bookmark icon on a result updates icon to filled state
- [x] 11.5 `BookmarksScreenTest`: verify bookmarked books appear in list; verify empty state shown when no bookmarks
- [x] 11.6 `BookDetailScreenTest`: verify note field visible for bookmarked book and hidden for non-bookmarked book
- [x] 11.7 `BookDetailScreenTest`: verify character counter displays correctly ("0/300") and updates as user types; verify input blocked at 300 chars
