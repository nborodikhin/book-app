## ADDED Requirements

### Requirement: A manual testing runbook exists at testing.md
The repository SHALL contain a `testing.md` file at the root that lists, as an unchecked Markdown checklist, only the user-visible scenarios that are NOT covered by automated tests. The runbook SHALL NOT list automated scenarios — automated coverage is implicit (run `./gradlew test connectedAndroidTest`). The runbook SHALL be organized by screen or feature area.

#### Scenario: testing.md is present in the repo
- **WHEN** a developer clones the repository
- **THEN** `testing.md` is present at the repo root and contains at least one manual checklist item

#### Scenario: Runbook covers scroll-anchor behavior
- **WHEN** a developer opens testing.md
- **THEN** they find a checklist item for verifying the bookmark icon remains visible after scrolling on BookDetailScreen

#### Scenario: Runbook covers end-to-end bookmark lifecycle
- **WHEN** a developer opens testing.md
- **THEN** they find checklist steps for: searching for a book → bookmarking it → verifying it appears in Bookmarks → adding a note → unbookmarking → verifying the note is cleared and the book is removed from Bookmarks

#### Scenario: Runbook covers configuration-change note persistence
- **WHEN** a developer opens testing.md
- **THEN** they find a checklist item for rotating the device while a note is being typed on BookDetailScreen and verifying the note text is preserved

#### Scenario: Runbook covers network error and retry flows
- **WHEN** a developer opens testing.md
- **THEN** they find checklist items for: disabling network during a search, verifying the error message and Retry button appear, re-enabling network and tapping Retry, verifying results load

#### Scenario: Runbook covers cover image loading
- **WHEN** a developer opens testing.md
- **THEN** they find a checklist item confirming cover images load from the Open Library CDN for books that have a cover ID

### Requirement: testing.md and automated tests are kept in sync with implementation
Whenever implementation changes affect a scenario listed in `testing.md` or the automated test suites, the corresponding runbook item and/or test SHALL be updated in the same commit or PR. This is a normative requirement that applies to all changes touching `SearchScreen`, `BookDetailScreen`, `BookmarksScreen`, `BookRepository`, or any of their ViewModels.

#### Scenario: Runbook updated when UI scenario changes
- **WHEN** a developer changes behavior covered by a manual checklist item
- **THEN** the corresponding item in `testing.md` is updated in the same change

#### Scenario: Automated tests updated when spec scenario changes
- **WHEN** a developer modifies a feature whose behavior is covered by a unit or instrumentation test
- **THEN** the affected test is updated in the same change and the test suite continues to pass
