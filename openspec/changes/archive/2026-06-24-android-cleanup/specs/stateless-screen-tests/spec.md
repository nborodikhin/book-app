## ADDED Requirements

### Requirement: Screen test suites are kept in sync with spec changes
`SearchScreenTest`, `BookDetailScreenTest`, and `BookmarksScreenTest` SHALL be updated whenever the corresponding screen spec is modified, and any new spec scenario for those screens SHALL have a corresponding instrumented test case. This requirement is enforced as a condition of archiving any change that modifies `book-search`, `bookmarks`, `note-edit-state`, `bookmarks-filter`, or `stateless-screen-tests` specs.

#### Scenario: New spec scenario has a corresponding test
- **WHEN** a spec scenario is added to `book-search`, `bookmarks`, or `note-edit-state`
- **THEN** a corresponding test case is added to the relevant `*ScreenTest` class in the same change

#### Scenario: Modified spec scenario results in test update
- **WHEN** an existing spec scenario in one of the covered specs is modified
- **THEN** the corresponding test in the relevant `*ScreenTest` class is updated to reflect the new expected behavior
