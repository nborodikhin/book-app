## ADDED Requirements

### Requirement: Bookmarks tab displays saved books
The Bookmarks tab SHALL display a list of all bookmarked books. The list SHALL be available offline using locally stored data.

#### Scenario: Bookmarked books shown
- **WHEN** the user opens the Bookmarks tab
- **THEN** all bookmarked books are displayed using locally stored data

#### Scenario: Empty bookmarks list
- **WHEN** no books have been bookmarked
- **THEN** an empty state message is shown (e.g., "No bookmarks yet")

### Requirement: Bookmark list item display
Each bookmark list item SHALL display: book cover image, author(s), title, and a filled bookmark icon.

#### Scenario: Bookmark item with cover
- **WHEN** a bookmarked book has a stored cover URL
- **THEN** the cover image is loaded and displayed via Coil

#### Scenario: Bookmark item without cover
- **WHEN** a bookmarked book has no cover URL
- **THEN** a placeholder image is shown

### Requirement: Book data fetched and stored on first bookmark
When a book is bookmarked for the first time, the app SHALL fetch the book's detail data from OpenLibrary (title, author(s), synopsis, cover URL) and store it in Room DB. Cover images SHALL NOT be pre-downloaded.

#### Scenario: First bookmark triggers data fetch
- **WHEN** the user bookmarks a book that has no local data
- **THEN** the app fetches work detail from OpenLibrary and stores it in Room DB

#### Scenario: Re-bookmarking does not re-fetch
- **WHEN** the user bookmarks a book that already has local data
- **THEN** no network request is made; existing local data is used

### Requirement: Bookmarked book data is never deleted
Data stored in Room DB for bookmarked books SHALL persist indefinitely and SHALL NOT be deleted, even if the book is unbookmarked.

#### Scenario: Data retained after unbookmark
- **WHEN** the user removes a bookmark
- **THEN** the book's data remains in Room DB and the book disappears from the Bookmarks tab

### Requirement: Bookmark state persisted in DataStore
The bookmarked/unbookmarked state for each book SHALL be persisted in DataStore and survive app restarts.

#### Scenario: Bookmark state survives restart
- **WHEN** the user bookmarks a book and restarts the app
- **THEN** the book is still shown as bookmarked

#### Scenario: Unbookmark state survives restart
- **WHEN** the user removes a bookmark and restarts the app
- **THEN** the book is no longer shown as bookmarked

### Requirement: Navigate to book detail from bookmarks
Tapping a bookmark list item SHALL open the book detail screen for that book.

#### Scenario: Tap on bookmark item
- **WHEN** the user taps a bookmarked book in the Bookmarks tab
- **THEN** the book detail screen is opened for that book
