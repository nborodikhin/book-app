## 1. Project Setup & Dependencies

- [ ] 1.1 Add dependencies to `app/build.gradle.kts`: Hilt, Room, DataStore, Retrofit, kotlinx.serialization, Coil, Compose Navigation
- [ ] 1.2 Add Hilt plugin to `build.gradle.kts` and `settings.gradle.kts`
- [ ] 1.3 Add KSP plugin for Room and Hilt annotation processing
- [ ] 1.4 Annotate `Application` class with `@HiltAndroidApp`
- [ ] 1.5 Add test dependencies: `kotlinx-coroutines-test`, `mockito-kotlin`, `turbine`, `robolectric` (unit); `androidx.compose.ui:ui-test-junit4`, `room-testing`, `androidx.compose.ui:ui-test-manifest` (instrumentation)

## 2. Data Layer — Network

- [ ] 2.1 Create `OpenLibraryApi` Retrofit interface with `searchBooks(query, page, limit)` and `getWork(workId)` endpoints
- [ ] 2.2 Create `SearchResponse` and `WorkDetailResponse` data classes (kotlinx.serialization)
- [ ] 2.3 Create Hilt `NetworkModule` providing `Retrofit` and `OpenLibraryApi` singletons

## 3. Data Layer — Local Storage

- [ ] 3.1 Create `BookEntity` Room entity (workId, title, authors, synopsis, coverUrl)
- [ ] 3.2 Create `BookDao` with insert and query methods
- [ ] 3.3 Create `AppDatabase` Room database class
- [ ] 3.4 Create Hilt `DatabaseModule` providing `AppDatabase` and `BookDao`
- [ ] 3.5 Create Hilt `DataStoreModule` providing `DataStore<Preferences>` singleton

## 4. Repository

- [ ] 4.1 Create `BookRepository` with in-memory search cache (`Map<String, List<SearchResult>>`)
- [ ] 4.2 Implement `search(query, page)` — checks cache first, falls back to network
- [ ] 4.3 Implement `getBookDetail(workId)` — checks Room first, falls back to network
- [ ] 4.4 Implement `setBookmarked(workId, bookmarked)` — updates DataStore; fetches and stores work detail in Room on first bookmark
- [ ] 4.5 Implement `isBookmarked(workId): Flow<Boolean>` from DataStore
- [ ] 4.6 Implement `getNote(workId): Flow<String>` and `setNote(workId, note)` via DataStore
- [ ] 4.7 Implement `getBookmarkedBooks(): Flow<List<BookEntity>>` from Room

## 5. Navigation & App Shell

- [ ] 5.1 Create `MainActivity` with `@AndroidEntryPoint` and set Compose content
- [ ] 5.2 Create `BookshelfApp` composable with `NavHost` and bottom navigation bar (Search, Bookmarks tabs)
- [ ] 5.3 Define nav routes: `search`, `bookmarks`, `book/{workId}`

## 6. Search Feature

- [ ] 6.1 Create `SearchViewModel` with `searchQuery` StateFlow and 2-second debounce trigger
- [ ] 6.2 Implement pagination logic in `SearchViewModel` (page counter, append results, detect end of list)
- [ ] 6.3 Create `SearchScreen` composable with text input, `LazyColumn` of results, loading spinner, and error state
- [ ] 6.4 Create `BookListItem` composable (cover image via Coil, author, title, bookmark icon toggle)
- [ ] 6.5 Wire scroll-near-end detection in `SearchScreen` to trigger next page load
- [ ] 6.6 Wire bookmark toggle in `SearchScreen` to `BookRepository.setBookmarked`

## 7. Bookmarks Feature

- [ ] 7.1 Create `BookmarksViewModel` exposing `bookmarkedBooks: Flow<List<BookEntity>>`
- [ ] 7.2 Create `BookmarksScreen` composable with `LazyColumn` of bookmarked books and empty state
- [ ] 7.3 Reuse `BookListItem` composable for bookmark list items

## 8. Book Detail Feature

- [ ] 8.1 Create `BookDetailViewModel` accepting `workId`; loads local data first, fetches from network if absent
- [ ] 8.2 Create `BookDetailScreen` composable: cover with bookmark icon overlay, author, title, synopsis, note field
- [ ] 8.3 Show note field only when book is bookmarked; enforce 300-char max; display live character counter ("X/300"); auto-save note on change via DataStore
- [ ] 8.4 Wire bookmark toggle on detail screen to `BookRepository.setBookmarked`
- [ ] 8.5 Show loading spinner while detail data is being fetched; show fallbacks for missing synopsis/cover

## 9. Polish & Error Handling

- [ ] 9.1 Add placeholder and error drawables for Coil image loading
- [ ] 9.2 Verify error state UI in Search (error icon + retry message)
- [ ] 9.3 Verify empty state UI in Bookmarks tab
- [ ] 9.4 Verify note field appears/disappears correctly when toggling bookmark on detail screen
- [ ] 9.5 Write README.md with project description, tech stack, and OpenLibrary API usage

## 10. Unit Tests (JVM / Robolectric)

- [ ] 10.1 `BookRepositoryTest`: verify search cache hit skips network call; cache miss calls API and stores result
- [ ] 10.2 `BookRepositoryTest`: verify `setBookmarked(true)` on a new book fetches work detail and inserts into Room; second call skips fetch
- [ ] 10.3 `BookRepositoryTest`: verify `setBookmarked(false)` clears bookmark flag in DataStore but does not delete Room entity
- [ ] 10.4 `BookRepositoryTest`: verify `getNote` / `setNote` round-trip through DataStore; verify note cleared on unbookmark
- [ ] 10.5 `SearchViewModelTest`: verify debounce — rapid input changes produce only one search call after 2 s idle (use `advanceTimeBy` + `StandardTestDispatcher`)
- [ ] 10.6 `SearchViewModelTest`: verify pagination — scrolling near end appends next page; no request sent when last page already loaded
- [ ] 10.7 `SearchViewModelTest`: verify loading state emitted during fetch; error state emitted on network failure
- [ ] 10.8 `BookDetailViewModelTest`: verify local-first — bookmarked book data served from Room without network call
- [ ] 10.9 `BookDetailViewModelTest`: verify non-local book triggers network fetch and shows loading state
- [ ] 10.10 `BookDetailViewModelTest`: verify note capped at 300 chars — input beyond limit is truncated/ignored

## 11. Instrumentation Tests (on-device)

- [ ] 11.1 `BookDaoTest`: insert a `BookEntity` and verify it is retrieved correctly; verify entity is not deleted after unbookmark (no delete DAO method)
- [ ] 11.2 `BookDaoTest`: verify `getBookmarkedBooks()` Flow emits updated list when new entities are inserted — use in-memory `Room.inMemoryDatabaseBuilder`
- [ ] 11.3 `SearchScreenTest`: verify typing in search field shows results list after debounce; verify spinner shown during load; verify error state shown on failure
- [ ] 11.4 `SearchScreenTest`: verify tapping bookmark icon on a result updates icon to filled state
- [ ] 11.5 `BookmarksScreenTest`: verify bookmarked books appear in list; verify empty state shown when no bookmarks
- [ ] 11.6 `BookDetailScreenTest`: verify note field visible for bookmarked book and hidden for non-bookmarked book
- [ ] 11.7 `BookDetailScreenTest`: verify character counter displays correctly ("0/300") and updates as user types; verify input blocked at 300 chars
