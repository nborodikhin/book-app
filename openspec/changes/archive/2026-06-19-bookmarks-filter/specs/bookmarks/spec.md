## MODIFIED Requirements

### Requirement: Bookmarks tab displays saved books
The Bookmarks tab SHALL display a list of all bookmarked books. The list SHALL be available offline using locally stored data. A text filter field SHALL appear above the list and narrow the displayed books in real time.

#### Scenario: Bookmarked books shown
- **WHEN** the user opens the Bookmarks tab
- **THEN** all bookmarked books are displayed using locally stored data

#### Scenario: Empty bookmarks list
- **WHEN** no books have been bookmarked
- **THEN** an empty state message is shown (e.g., "No bookmarks yet")

#### Scenario: No filter matches
- **WHEN** the user has bookmarks but the current filter query matches none of them
- **THEN** a "No results" message is shown (distinct from the "no bookmarks" empty state)
