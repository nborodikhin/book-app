# detail-bookmark-anchor Specification

## Purpose
TBD - created by archiving change screen-previews-and-ui-polish. Update Purpose after archive.
## Requirements
### Requirement: Bookmark icon is always visible on BookDetailScreen regardless of scroll position
The bookmark `IconButton` on `BookDetailScreen` SHALL be positioned as a static overlay anchored to the top-end corner of the screen's content area, so it remains visible when the user scrolls the book detail content.

#### Scenario: Bookmark icon visible at top of page
- **WHEN** `BookDetailScreen` is displayed and the user has not scrolled
- **THEN** the bookmark icon is visible at the top-end corner of the content area

#### Scenario: Bookmark icon visible after scrolling
- **WHEN** the user scrolls down on `BookDetailScreen` past the cover image
- **THEN** the bookmark icon remains visible at the top-end corner and does not scroll off screen

### Requirement: Bookmark icon is removed from the scrollable cover area
The cover `Box` / `AsyncImage` in the scrollable `Column` on `BookDetailScreen` SHALL NOT contain the bookmark `IconButton`; the icon SHALL only appear in the static overlay.

#### Scenario: Cover area has no bookmark icon
- **WHEN** `BookDetailScreen` is rendered
- **THEN** the scrollable cover image area does not contain a bookmark icon

### Requirement: Bookmark icon reflects current bookmark state
The static bookmark overlay icon SHALL display in the filled state when the book is bookmarked and in the outline/unfilled state when it is not.

#### Scenario: Filled icon when bookmarked
- **WHEN** `isBookmarked` is `true`
- **THEN** the bookmark icon is displayed in its filled variant

#### Scenario: Outline icon when not bookmarked
- **WHEN** `isBookmarked` is `false`
- **THEN** the bookmark icon is displayed in its outline/unfilled variant

### Requirement: Tapping the bookmark icon toggles the bookmark state
Tapping the static bookmark `IconButton` SHALL call `onToggleBookmark`, which the caller wires to the ViewModel.

#### Scenario: Tap triggers toggle callback
- **WHEN** the user taps the bookmark icon
- **THEN** `onToggleBookmark` is invoked exactly once

