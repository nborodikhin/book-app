## ADDED Requirements

### Requirement: Bookmarks filter logic is covered by ViewModel unit tests
`BookmarksViewModelTest` SHALL have test cases covering each scenario in the `bookmarks-filter` spec, using a `MutableStateFlow` of `BookEntity` items and Mockito mocks. Tests SHALL be updated whenever the filter spec is modified.

#### Scenario: Unit test exists for each filter spec scenario
- **WHEN** the `BookmarksViewModelTest` test suite is run
- **THEN** tests pass for: empty query returns all books, filter by title, filter by author, case-insensitive matching, no-match returns empty list, and clearing filter restores all books

#### Scenario: New filter scenario has a unit test
- **WHEN** a new scenario is added to the `bookmarks-filter` spec
- **THEN** a corresponding unit test is added to `BookmarksViewModelTest` in the same change
