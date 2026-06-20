# Bookmarks Filter Spec

## Purpose

Defines the behavior of the real-time text filter on the Bookmarks screen, including case-insensitive matching against book title and author fields, immediate list updates, and empty/no-match states.

## Requirements

### Requirement: Bookmarks list is filterable by title and author
The Bookmarks screen SHALL display a text input field above the list that filters displayed books in real time. Filtering SHALL be case-insensitive and match any substring of the book title or any author name.

#### Scenario: Filter matches by title
- **WHEN** the user types a query that is a substring of at least one bookmarked book's title
- **THEN** only books whose title contains the query (case-insensitive) are displayed

#### Scenario: Filter matches by author
- **WHEN** the user types a query that is a substring of at least one bookmarked book's author field
- **THEN** only books whose author contains the query (case-insensitive) are displayed

#### Scenario: Filter is immediate
- **WHEN** the user changes the text in the filter field
- **THEN** the list updates immediately without any delay

#### Scenario: Empty query shows all books
- **WHEN** the filter field is empty
- **THEN** all bookmarked books are displayed

#### Scenario: No matches
- **WHEN** the filter query matches no bookmarked books
- **THEN** a "No results" message is displayed instead of the list
