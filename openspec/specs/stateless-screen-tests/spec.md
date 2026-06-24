# stateless-screen-tests Specification

## Purpose
TBD - created by archiving change screen-previews-and-ui-polish. Update Purpose after archive.
## Requirements
### Requirement: BookDetailScreen has composable-level instrumented tests covering spec scenarios
`BookDetailScreenTest` SHALL test `BookDetailScreen` UI behavior by calling a stateless content overload directly via `createComposeRule`, with no Hilt or ViewModel required.

#### Scenario: Note field visible when bookmarked
- **WHEN** `BookDetailScreenContent` is rendered with `isBookmarked = true` and a `Success` state
- **THEN** the note `OutlinedTextField` is displayed

#### Scenario: Note field hidden when not bookmarked
- **WHEN** `BookDetailScreenContent` is rendered with `isBookmarked = false` and a `Success` state
- **THEN** the note `OutlinedTextField` is not displayed

#### Scenario: Character counter shows initial count
- **WHEN** `BookDetailScreenContent` is rendered with `note = ""`
- **THEN** the counter text `"0/300"` is displayed

#### Scenario: Character counter updates as text changes
- **WHEN** the user types text into the note field
- **THEN** the counter updates to reflect the current character count in the form `"N/300"`

#### Scenario: Input capped at 300 characters in UI
- **WHEN** text longer than 300 characters is entered into the note field
- **THEN** the field content does not exceed 300 characters

#### Scenario: Loading state renders correctly
- **WHEN** `BookDetailScreenContent` is rendered with a `Loading` ui state
- **THEN** a loading indicator is displayed and no book content is shown

#### Scenario: Error state renders correctly
- **WHEN** `BookDetailScreenContent` is rendered with an `Error` ui state
- **THEN** an error message is displayed and no book content is shown

### Requirement: SearchScreen has composable-level instrumented tests covering spec scenarios
`SearchScreenTest` SHALL test `SearchScreen` UI behavior by calling a stateless content overload directly via `createComposeRule`, with no Hilt or ViewModel required.

#### Scenario: Loading spinner visible during Loading state
- **WHEN** `SearchScreenContent` is rendered with a `Loading` ui state
- **THEN** a loading spinner or progress indicator is visible

#### Scenario: Error message visible during Error state
- **WHEN** `SearchScreenContent` is rendered with an `Error` ui state
- **THEN** an error message is visible

#### Scenario: Results list visible during Success state
- **WHEN** `SearchScreenContent` is rendered with a `Success` ui state containing items
- **THEN** the results list is visible with the expected items

#### Scenario: Bookmark icon filled when item is bookmarked
- **WHEN** a search result item has `isBookmarked = true`
- **THEN** the item's bookmark icon is in the filled state

#### Scenario: Bookmark icon outline when item is not bookmarked
- **WHEN** a search result item has `isBookmarked = false`
- **THEN** the item's bookmark icon is in the outline/unfilled state

### Requirement: BookmarksScreen has composable-level instrumented tests covering spec scenarios
`BookmarksScreenTest` SHALL test `BookmarksScreen` UI behavior by calling a stateless content overload directly via `createComposeRule`, with no Hilt or ViewModel required.

#### Scenario: Empty state message shown when books list is empty
- **WHEN** `BookmarksScreenContent` is rendered with an empty `books` list
- **THEN** an empty-state message is displayed and no list items are shown

#### Scenario: List items rendered when books list is non-empty
- **WHEN** `BookmarksScreenContent` is rendered with a non-empty `books` list
- **THEN** the list items are displayed

### Requirement: Screen tests do not require Hilt, ViewModel, or network
All three screen test classes SHALL use `createComposeRule` with stateless content overloads and SHALL NOT use Hilt test rules, `FakeBookRepository`, or any network-dependent component.

#### Scenario: Tests run without Hilt setup
- **WHEN** any of the three screen test classes is executed
- **THEN** no Hilt module installation or binding is required and the tests complete without Hilt errors

### Requirement: Screen test suites are kept in sync with spec changes
`SearchScreenTest`, `BookDetailScreenTest`, and `BookmarksScreenTest` SHALL be updated whenever the corresponding screen spec is modified, and any new spec scenario for those screens SHALL have a corresponding instrumented test case. This requirement is enforced as a condition of archiving any change that modifies `book-search`, `bookmarks`, `note-edit-state`, `bookmarks-filter`, or `stateless-screen-tests` specs.

#### Scenario: New spec scenario has a corresponding test
- **WHEN** a spec scenario is added to `book-search`, `bookmarks`, or `note-edit-state`
- **THEN** a corresponding test case is added to the relevant `*ScreenTest` class in the same change

#### Scenario: Modified spec scenario results in test update
- **WHEN** an existing spec scenario in one of the covered specs is modified
- **THEN** the corresponding test in the relevant `*ScreenTest` class is updated to reflect the new expected behavior

