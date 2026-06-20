## Context

Three screens (`SearchScreen`, `BookmarksScreen`, `BookDetailScreen`) and one shared component (`BookListItem`) have no `@Preview` composables, making layout iteration slow. Additionally, `BookDetailScreen` currently wires the note `OutlinedTextField` directly to `viewModel.note: StateFlow<String>`, which means every persisted change round-trips through the database and back into the UI—causing cursor jumps and unnecessary recompositions. The bookmark `IconButton` sits inside the scrollable `Column` (inside a cover `Box`), so it scrolls off-screen with the cover image.

## Goals / Non-Goals

**Goals:**
- Add `@Preview` functions for all UI states of each screen and `BookListItem`
- Make the screen the sole owner of note text state after initialization, with VM only notified of changes (not driving recomposition)
- Pin the bookmark icon at the top-end corner of `BookDetailScreen`, visible regardless of scroll position

**Non-Goals:**
- Refactoring screens to be fully stateless / extracting separate content composables for architecture reasons
- Changing how notes are persisted (still auto-saved via `onNoteChange` → `repository.setNote`)
- Addressing any other scroll or layout issues on detail screen

## Decisions

### 1. Preview strategy for Hilt-injected screens

`@Preview` cannot instantiate Hilt ViewModels. **Decision**: add preview-specific overloads that accept state directly (no ViewModel).

For each screen, add a private overloaded composable that takes data (or a fake state object) instead of a ViewModel, and wire the Hilt version through it. The `@Preview` functions call the data-accepting overload with hardcoded fake data.

Alternative considered: extract a separate `*Content` composable per screen. Rejected — it's a larger refactor than needed and forces a public API change. The private overload keeps the public API unchanged.

**For `BookListItem`**: already stateless, so `@Preview` can call it directly.

**Preview states to cover**:
- `SearchScreen`: Idle, Loading, Success (with items), Error
- `BookmarksScreen`: Empty, Non-empty list
- `BookDetailScreen`: Loading, Error, Success (not bookmarked), Success (bookmarked, with note field)
- `BookListItem`: bookmarked, not bookmarked

### 2. Note field local state initialization

**Decision**: use `remember { mutableStateOf("") }` and a `remember { mutableStateOf(false) }` initialized flag in `BookDetailScreen`. A `LaunchedEffect(Unit)` reads the **first** emission from `viewModel.note`, sets `noteText`, then sets the flag to `true`. The `OutlinedTextField` is `enabled = noteInitialized`. Keystrokes update local state directly and call `viewModel.onNoteChange(...)` for persistence; the VM's `note` flow is never observed again by the text field.

```kotlin
var noteText by remember { mutableStateOf("") }
var noteInitialized by remember { mutableStateOf(false) }

LaunchedEffect(Unit) {
    noteText = viewModel.note.filterNotNull().first()
    noteInitialized = true
}

OutlinedTextField(
    value = noteText,
    enabled = noteInitialized,
    onValueChange = { noteText = it; viewModel.onNoteChange(it) },
    ...
)
```

`viewModel.note` is `StateFlow<String?>`. Two distinct meanings of `null` must be kept apart:
- **Sentinel `null`** (initial StateFlow value, `""` would have been swallowed by `.first()` immediately) — means "not yet loaded from DB"
- **DB `null`** — means "no note row exists for this book" (e.g., a book with no saved note, or one that was just bookmarked)

To prevent `filterNotNull().first()` from hanging forever on a DB `null`, the ViewModel maps the DB value before `stateIn`:

```kotlin
val note: StateFlow<String?> = repository.getNote(workId)
    .map { it ?: "" }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
```

The upstream `repository.getNote()` may emit `null` (no note in DB). `.map { it ?: "" }` converts that to `""` so the StateFlow only ever holds `null` (sentinel) or a real string. `filterNotNull().first()` then always resolves once the DB emits — even for books with no saved note or for books just being bookmarked for the first time.

This reads the persisted value exactly once on composition, then the screen owns the state. Blocking edits until initialized prevents the user from typing into a stale empty field that would then be overwritten by the arriving value. `rememberSaveable` is not needed — the ViewModel survives configuration changes and holds the current note value, so `LaunchedEffect(Unit)` re-fires on activity recreation and reinitializes `noteText` from the VM.

Alternative considered: `collectAsStateWithLifecycle` + debounce on saves (VM ignores incoming updates from the flow after saving). Rejected — still round-trips through DB and StateFlow, still risks cursor reset.

### 3. Bookmark icon anchor

**Decision**: wrap the `Scaffold`'s content lambda body in a `Box`, place the scrollable `Column` inside it, and overlay the `IconButton` at `Alignment.TopEnd` with a fixed `padding(8.dp)`. The cover `Box`/`AsyncImage` remains in the scroll column but without the bookmark button.

```
Scaffold {
  Box(fillMaxSize) {
    Column(verticalScroll) {
      Box(cover image only — no button)
      ...content...
    }
    IconButton(Modifier.align(TopEnd).padding(8.dp))  // static overlay
  }
}
```

Alternative considered: adding bookmark to `TopAppBar` actions. Rejected — `TopAppBar` is stylistically inappropriate for a bookmark toggle (it's a content action, not a nav action), and the visual design intent places it over the cover area.

### 4. Reuse stateless overloads for composable-level tests

The private stateless overloads introduced for `@Preview` (decision 1) are also used in instrumented tests. The existing shallow `*ScreenTest` files are replaced — they currently test scaffolding (bottom nav renders, search field exists) rather than spec scenarios.

Each test calls the stateless overload directly via `createComposeRule` (no Hilt, no ViewModel, no device network):

```kotlin
composeRule.setContent {
    BookDetailScreenContent(
        uiState = DetailUiState.Success(sampleBook),
        isBookmarked = true,
        note = "hello",
        onBack = {}, onToggleBookmark = {}, onNoteChange = {}
    )
}
composeRule.onNodeWithText("My note").assertIsDisplayed()
composeRule.onNodeWithText("5/300").assertIsDisplayed()
```

**Scenarios now covered by tests (previously untested):**
- `BookDetailScreenTest`: note field visible/hidden based on `isBookmarked`; character counter shows `"0/300"` and updates as text changes; input capped at 300 chars; loading and error states render correctly
- `SearchScreenTest`: loading spinner visible during `Loading` state; error message visible during `Error` state; results list visible during `Success` state; bookmark icon filled vs. outline per `isBookmarked`
- `BookmarksScreenTest`: empty state message shown when `books` is empty; list items rendered when `books` is non-empty

**What these tests don't cover** (and don't need to — already covered by unit tests):
- ViewModel logic (debounce, pagination, note truncation) — covered by `SearchViewModelTest`, `BookDetailViewModelTest`
- Repository correctness — covered by `BookRepositoryTest`, `BookDaoTest`

Alternative considered: Hilt integration tests with `FakeBookRepository` (`@UninstallModules` + `@BindValue`). Not chosen — the stateless overload approach covers the same UI scenarios without Hilt overhead, and ViewModel behavior is already unit-tested.

## Risks / Trade-offs

- **`LaunchedEffect(Unit)` race**: if `viewModel.note` hasn't emitted by the time the coroutine starts, `first()` will suspend until it does, which is correct. The field is disabled until initialization completes, so the user cannot type into a stale empty field that would be overwritten by the arriving value. → Mitigated by `enabled = noteInitialized`.
- **Preview overloads add surface area**: private overloads are hidden from the public API but add slight indirection. → Acceptable trade-off for preview coverage without architecture churn.
- **300-char limit**: if the user manages to paste >300 chars via clipboard before the VM trims it, local state will hold the overlong string momentarily. The VM's `onNoteChange` trims before saving, so persistence is safe; add client-side length guard too for consistency.

## Migration Plan

No data migration required. Changes are purely UI-layer. No new dependencies needed (`compose-ui-tooling-preview` is already on the classpath via existing preview infra). Deploy as a standard feature branch.
