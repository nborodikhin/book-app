# note-edit-state Specification

## Purpose
TBD - created by archiving change screen-previews-and-ui-polish. Update Purpose after archive.
## Requirements
### Requirement: Note text field initializes from ViewModel exactly once
`BookDetailScreen` SHALL initialize the note `OutlinedTextField` value from the first emission of `viewModel.note`, then own the state independently; it SHALL NOT re-read from the ViewModel on subsequent recompositions or after configuration changes.

#### Scenario: Note field shows persisted value on first composition
- **WHEN** `BookDetailScreen` is composed for the first time
- **THEN** the note field displays the value returned by the first emission of `viewModel.note`

#### Scenario: Note field is not reset after configuration change
- **WHEN** the device is rotated or the activity is otherwise recreated
- **THEN** the note field reinitializes from `viewModel.note` (which holds the current value in the surviving ViewModel) and does not show an empty or stale value

#### Scenario: Typing does not trigger reinitialization
- **WHEN** the user types characters in the note field after initialization
- **THEN** the text field value reflects the typed characters and does not revert to the ViewModel's current `note` value

### Requirement: Note text field is disabled until initialization completes
The note `OutlinedTextField` SHALL be disabled (non-editable) until the first emission from `viewModel.note` has been received and applied to local state.

#### Scenario: Field is disabled before first emission
- **WHEN** `BookDetailScreen` is composed and `viewModel.note` has not yet emitted its first value
- **THEN** the note `OutlinedTextField` is in a disabled state and does not accept user input

#### Scenario: Field is enabled after initialization
- **WHEN** `viewModel.note` emits its first value and local state is set
- **THEN** the note `OutlinedTextField` becomes enabled and accepts user input

#### Scenario: Field is enabled when bookmarking from the detail screen
- **WHEN** the user bookmarks a book directly from `BookDetailScreen` (book was not bookmarked on entry)
- **THEN** the note field appears enabled, not in a permanently disabled state

### Requirement: Keystrokes update local state and notify ViewModel
Each keystroke in the note field SHALL update local `remember`-based state immediately and call `viewModel.onNoteChange(text)` for persistence; the ViewModel's `note` flow SHALL NOT be re-observed after initialization.

#### Scenario: Keystroke updates local state
- **WHEN** the user types a character in the note field
- **THEN** the field immediately displays the updated text without waiting for a ViewModel round-trip

#### Scenario: Keystroke triggers ViewModel notification
- **WHEN** the user types a character in the note field
- **THEN** `viewModel.onNoteChange` is called with the updated text for persistence

### Requirement: Note field enforces a 300-character maximum
The note `OutlinedTextField` SHALL reject input beyond 300 characters at the UI layer, consistent with the ViewModel's persistence limit.

#### Scenario: Input capped at 300 characters
- **WHEN** the user attempts to type or paste text that would exceed 300 characters
- **THEN** the field content is capped at 300 characters and excess characters are not displayed

#### Scenario: Character counter reflects current length
- **WHEN** the note field contains N characters
- **THEN** a counter showing `"N/300"` is displayed

