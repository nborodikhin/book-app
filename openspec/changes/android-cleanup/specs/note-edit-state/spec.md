## ADDED Requirements

### Requirement: Note-edit-state behaviors are covered by ViewModel unit tests
`BookDetailViewModelTest` SHALL have test cases covering bookmark-toggle persistence and note lifecycle behaviors from the `note-edit-state` spec. Existing tests cover note initialization and 300-char cap; the following are also required.

#### Scenario: onToggleBookmark true calls repository setBookmarked
- **WHEN** `viewModel.onToggleBookmark(true)` is called with a known workId
- **THEN** `repository.setBookmarked(workId, true, ...)` is invoked exactly once

#### Scenario: onToggleBookmark false calls repository setBookmarked
- **WHEN** `viewModel.onToggleBookmark(false)` is called
- **THEN** `repository.setBookmarked(workId, false)` is invoked exactly once

#### Scenario: New note-edit-state scenario has a unit test
- **WHEN** a new scenario is added to the `note-edit-state` spec
- **THEN** a corresponding unit test is added to `BookDetailViewModelTest` in the same change
