# Book Search Spec

## Purpose

Defines the behavior of the Search tab, including text input with debounce, OpenLibrary integration, result display, bookmark toggling from results, pagination, caching, and error/loading states.

## Requirements

### Requirement: Text search input with debounce
The app SHALL provide a text input field on the Search tab. Search SHALL be triggered automatically 2 seconds after the user stops typing. Search SHALL NOT be triggered on every keystroke.

#### Scenario: Search triggers after idle period
- **WHEN** the user types in the search field and stops for 2 seconds
- **THEN** the app performs a search against OpenLibrary using the current input text

#### Scenario: Search does not trigger while typing
- **WHEN** the user is actively typing (changes within 2 seconds)
- **THEN** no search request is sent

#### Scenario: Empty search input
- **WHEN** the search field is empty or cleared
- **THEN** no search is performed and any previous results are cleared

### Requirement: Search by title and author
The app SHALL search OpenLibrary using the `/search.json` endpoint with the query applied across title and author fields.

#### Scenario: Results contain matching books
- **WHEN** a search completes successfully
- **THEN** results include books matching the query by title or author name

### Requirement: Search result item display
Each search result SHALL display: book cover image, author(s), title, and a bookmark icon (empty when not bookmarked, filled when bookmarked).

#### Scenario: Result with cover image
- **WHEN** a search result has a cover ID
- **THEN** the cover image is loaded and displayed via Coil

#### Scenario: Result without cover image
- **WHEN** a search result has no cover ID
- **THEN** a placeholder image is shown

#### Scenario: Result for bookmarked book
- **WHEN** a search result corresponds to a bookmarked book
- **THEN** the bookmark icon is shown as filled

#### Scenario: Result for non-bookmarked book
- **WHEN** a search result does not correspond to a bookmarked book
- **THEN** the bookmark icon is shown as empty (outline)

### Requirement: Bookmark toggle from search results
The user SHALL be able to toggle the bookmark state of a book directly from the search result list.

#### Scenario: Bookmarking from search result
- **WHEN** the user taps the bookmark icon on a non-bookmarked search result
- **THEN** the book is marked as bookmarked, the icon becomes filled, and book data is fetched and stored locally

#### Scenario: Unbookmarking from search result
- **WHEN** the user taps the filled bookmark icon on a bookmarked search result
- **THEN** the bookmark flag is cleared and the icon becomes empty (book data is retained)

### Requirement: Paginated search results
Search results SHALL be loaded in pages of 20. Additional pages SHALL be fetched automatically as the user scrolls near the end of the list.

#### Scenario: Initial search loads first page
- **WHEN** a search is triggered
- **THEN** the first page (20 results) is fetched and displayed

#### Scenario: Scroll triggers next page
- **WHEN** the user scrolls to within 5 items of the end of the current results
- **THEN** the next page is fetched and appended to the list

#### Scenario: No more results
- **WHEN** the API returns fewer results than the page size
- **THEN** no further page requests are made

### Requirement: In-memory search cache
Search results SHALL be cached in memory keyed by query and page number for the duration of the app lifecycle.

#### Scenario: Cached result served without network call
- **WHEN** the user performs the same search query again within the same app session
- **THEN** cached results are returned immediately without a network request

### Requirement: Loading state
While a search request is in flight, the app SHALL display a loading spinner.

#### Scenario: Spinner shown during fetch
- **WHEN** a search request is in progress
- **THEN** a spinner is visible and the result list is not shown

### Requirement: Error state
If a search request fails, the app SHALL display an error icon and a message prompting the user to search again.

#### Scenario: Network error during search
- **WHEN** a search request fails due to a network error
- **THEN** an error icon and "Something went wrong. Try searching again." message is displayed

### Requirement: Navigate to book detail from search
Tapping a search result item SHALL open the book detail screen for that book.

#### Scenario: Tap on search result
- **WHEN** the user taps a search result (not the bookmark icon)
- **THEN** the book detail screen is opened for the selected book
