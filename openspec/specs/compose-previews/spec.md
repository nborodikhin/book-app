# compose-previews Specification

## Purpose
TBD - created by archiving change screen-previews-and-ui-polish. Update Purpose after archive.
## Requirements
### Requirement: SearchScreen has previews for all major UI states
`SearchScreen` SHALL have `@Preview`-annotated composables covering the Idle, Loading, Success (with items), and Error states.

#### Scenario: Idle state preview renders
- **WHEN** the `SearchScreen` Idle preview is rendered in Android Studio
- **THEN** an empty search field with no results and no spinner is visible

#### Scenario: Loading state preview renders
- **WHEN** the `SearchScreen` Loading preview is rendered in Android Studio
- **THEN** a loading spinner (or progress indicator) is visible and no results list is shown

#### Scenario: Success state preview renders
- **WHEN** the `SearchScreen` Success preview is rendered in Android Studio
- **THEN** a list of book items is visible with at least one item showing title, author, and bookmark icon

#### Scenario: Error state preview renders
- **WHEN** the `SearchScreen` Error preview is rendered in Android Studio
- **THEN** an error message is visible and no results list is shown

### Requirement: BookmarksScreen has previews for all major UI states
`BookmarksScreen` SHALL have `@Preview`-annotated composables covering the Empty and Non-empty list states.

#### Scenario: Empty state preview renders
- **WHEN** the `BookmarksScreen` Empty preview is rendered in Android Studio
- **THEN** an empty-state message is visible and no list items are shown

#### Scenario: Non-empty state preview renders
- **WHEN** the `BookmarksScreen` Non-empty preview is rendered in Android Studio
- **THEN** a list of bookmarked items is visible

### Requirement: BookDetailScreen has previews for all major UI states
`BookDetailScreen` SHALL have `@Preview`-annotated composables covering the Loading, Error, Success (not bookmarked), and Success (bookmarked with note field) states.

#### Scenario: Loading state preview renders
- **WHEN** the `BookDetailScreen` Loading preview is rendered in Android Studio
- **THEN** a loading indicator is visible and no book content is shown

#### Scenario: Error state preview renders
- **WHEN** the `BookDetailScreen` Error preview is rendered in Android Studio
- **THEN** an error message is visible and no book content is shown

#### Scenario: Success not-bookmarked preview renders
- **WHEN** the `BookDetailScreen` Success (not bookmarked) preview is rendered in Android Studio
- **THEN** book details are visible, the bookmark icon is in the unfilled/outline state, and the note field is not shown

#### Scenario: Success bookmarked preview renders
- **WHEN** the `BookDetailScreen` Success (bookmarked) preview is rendered in Android Studio
- **THEN** book details are visible, the bookmark icon is in the filled state, and the note `OutlinedTextField` is shown

### Requirement: BookListItem has previews for bookmarked and unbookmarked states
`BookListItem` SHALL have `@Preview`-annotated composables for both the bookmarked and not-bookmarked states.

#### Scenario: Bookmarked item preview renders
- **WHEN** the `BookListItem` bookmarked preview is rendered in Android Studio
- **THEN** the item displays with a filled bookmark icon

#### Scenario: Not-bookmarked item preview renders
- **WHEN** the `BookListItem` not-bookmarked preview is rendered in Android Studio
- **THEN** the item displays with an outline/unfilled bookmark icon

### Requirement: Previews do not depend on Hilt or ViewModel
Preview composables SHALL compile and render without requiring Hilt injection or a live ViewModel instance.

#### Scenario: Preview builds without Hilt
- **WHEN** the preview module is compiled for Android Studio rendering
- **THEN** no Hilt or ViewModel instantiation is attempted and the preview renders successfully with hardcoded fake data

