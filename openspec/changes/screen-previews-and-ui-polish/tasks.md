## 1. Note field local state (BookDetailScreen)

- [x] 1.1 Add `var noteText by remember { mutableStateOf("") }` and `var noteInitialized by remember { mutableStateOf(false) }` locals in `BookDetailScreen`
- [x] 1.2 Add `LaunchedEffect(Unit)` that calls `viewModel.note.first()`, sets `noteText`, then sets `noteInitialized = true`
- [x] 1.3 Wire `OutlinedTextField` to use `noteText` as value, `enabled = noteInitialized`, and `onValueChange` that updates `noteText` and calls `viewModel.onNoteChange(it)`
- [x] 1.4 Add client-side 300-char cap in `onValueChange` (take at most 300 chars before passing to ViewModel)
- [x] 1.5 Remove any existing `collectAsStateWithLifecycle` observation of `viewModel.note` that was driving the text field

## 2. Bookmark icon anchor (BookDetailScreen)

- [x] 2.1 Remove the bookmark `IconButton` from the cover `Box` inside the scrollable `Column`
- [x] 2.2 Wrap the `Scaffold` content body in a `Box(Modifier.fillMaxSize())`
- [x] 2.3 Place the scrollable `Column` inside that outer `Box`
- [x] 2.4 Add the bookmark `IconButton` as a sibling overlay at `Modifier.align(Alignment.TopEnd).padding(8.dp)` inside the outer `Box`

## 3. Stateless content overloads (all screens)

- [x] 3.1 Extract a private `BookDetailScreenContent(uiState, isBookmarked, note, noteInitialized, onBack, onToggleBookmark, onNoteChange)` overload in `BookDetailScreen.kt`; wire the Hilt entry-point composable through it
- [x] 3.2 Extract a private `SearchScreenContent(uiState, onQueryChange, onBookmarkToggle)` overload in `SearchScreen.kt`; wire the Hilt entry-point composable through it
- [ ] 3.3 Extract a private `BookmarksScreenContent(books, onBookClick)` overload in `BookmarksScreen.kt`; wire the Hilt entry-point composable through it

## 4. Compose previews

- [x] 4.1 Add `@Preview` functions for `SearchScreen`: Idle, Loading, Success (with items), Error — each calls `SearchScreenContent` with hardcoded fake data
- [ ] 4.2 Add `@Preview` functions for `BookmarksScreen`: Empty, Non-empty — each calls `BookmarksScreenContent` with hardcoded fake data
- [x] 4.3 Add `@Preview` functions for `BookDetailScreen`: Loading, Error, Success (not bookmarked), Success (bookmarked with note) — each calls `BookDetailScreenContent` with hardcoded fake data
- [ ] 4.4 Add `@Preview` functions for `BookListItem`: bookmarked, not bookmarked — calls `BookListItem` directly

## 5. Composable-level instrumented tests

- [ ] 5.1 Replace `BookDetailScreenTest` with tests using `createComposeRule` + `BookDetailScreenContent`: note visible/hidden by `isBookmarked`; counter shows `"0/300"` and updates; input capped at 300 chars; loading and error states render
- [ ] 5.2 Replace `SearchScreenTest` with tests using `createComposeRule` + `SearchScreenContent`: loading spinner visible; error message visible; results list visible; bookmark icon filled vs outline
- [ ] 5.3 Replace `BookmarksScreenTest` with tests using `createComposeRule` + `BookmarksScreenContent`: empty state message shown; list items rendered when non-empty
