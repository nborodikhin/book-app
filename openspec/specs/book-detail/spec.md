# Book Detail Spec

## Purpose

Defines the behavior of the book detail screen, including layout, bookmark toggling, personal notes, navigation entry points, and data loading.

## Requirements

### Requirement: Book detail screen layout
The book detail screen SHALL display: book cover image with a bookmark icon overlaid, author(s), title, and synopsis.

#### Scenario: Detail screen shows all fields
- **WHEN** the book detail screen opens for a book with full data
- **THEN** cover image, bookmark icon, author(s), title, and synopsis are all displayed

#### Scenario: Missing synopsis fallback
- **WHEN** a book has no synopsis
- **THEN** "No description available." is shown in place of the synopsis

#### Scenario: Missing cover fallback
- **WHEN** a book has no cover image
- **THEN** a placeholder image is displayed

### Requirement: Bookmark toggle on detail screen
The book detail screen SHALL display a bookmark icon on the cover image. Tapping it SHALL toggle the bookmark state.

#### Scenario: Bookmark icon reflects current state
- **WHEN** the detail screen opens for a non-bookmarked book
- **THEN** the bookmark icon is shown as empty (outline)

#### Scenario: Bookmarking from detail screen
- **WHEN** the user taps the empty bookmark icon on the detail screen
- **THEN** the book is bookmarked, the icon becomes filled, and book data is stored locally

#### Scenario: Unbookmarking from detail screen
- **WHEN** the user taps the filled bookmark icon on the detail screen
- **THEN** the bookmark is removed and the icon becomes empty

### Requirement: Note field for bookmarked books
The book detail screen SHALL display an editable text field for a personal note, but ONLY when the book is bookmarked. Notes are limited to 300 characters. The note field SHALL display a character counter in the format `"<current>/<max>"` (e.g., `"10/300"`). Input beyond 300 characters SHALL be rejected.

#### Scenario: Note field visible for bookmarked book
- **WHEN** the detail screen is open for a bookmarked book
- **THEN** an editable note field is displayed with a character counter showing `"<length>/300"`

#### Scenario: Note field hidden for non-bookmarked book
- **WHEN** the detail screen is open for a non-bookmarked book
- **THEN** no note field is displayed

#### Scenario: Character counter updates as user types
- **WHEN** the user types in the note field
- **THEN** the counter updates in real time (e.g., `"0/300"` → `"5/300"`)

#### Scenario: Note at character limit
- **WHEN** the note reaches 300 characters
- **THEN** further input is rejected and the counter shows `"300/300"`

#### Scenario: Note saved automatically
- **WHEN** the user edits the note field
- **THEN** the note is persisted to DataStore

#### Scenario: Note persists across sessions
- **WHEN** the user enters a note, closes the app, and reopens the detail screen
- **THEN** the previously entered note is displayed

#### Scenario: Note cleared when book is unbookmarked then re-bookmarked
- **WHEN** a book is unbookmarked and later re-bookmarked
- **THEN** the note field is empty (previous note was cleared on unbookmark)

### Requirement: Detail screen accessible from both tabs
The book detail screen SHALL be reachable by tapping any book item in both the Search tab and the Bookmarks tab.

#### Scenario: Opened from search
- **WHEN** the user taps a book in the Search tab
- **THEN** the detail screen opens on top of the main screen

#### Scenario: Opened from bookmarks
- **WHEN** the user taps a book in the Bookmarks tab
- **THEN** the detail screen opens on top of the main screen

### Requirement: Detail data loading
When navigating to a book detail that has no local data (not yet bookmarked), the app SHALL fetch the work detail from OpenLibrary and display a loading state while fetching.

#### Scenario: Loading state for non-local book
- **WHEN** the detail screen opens for a book with no local data
- **THEN** a loading spinner is shown while the work detail is fetched

#### Scenario: Local data used immediately for bookmarked books
- **WHEN** the detail screen opens for a bookmarked book
- **THEN** locally stored data is displayed immediately without a network request
