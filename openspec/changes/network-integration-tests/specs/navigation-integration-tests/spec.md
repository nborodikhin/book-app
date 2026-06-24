## ADDED Requirements

### Requirement: Search results load from a MockWebServer response
`BookshelfIntegrationTest` SHALL verify that entering a query in the search field causes the app to display results parsed from a MockWebServer-enqueued response.

#### Scenario: Search results appear after successful network response
- **WHEN** MockWebServer has a search response enqueued and the user types a query
- **THEN** the result titles from the enqueued response are visible in the list

### Requirement: Network error during search shows error UI and retry recovers
`BookshelfIntegrationTest` SHALL verify the full error → retry → success flow without a real network.

#### Scenario: Network error shows error message
- **WHEN** MockWebServer enqueues a connection error and the user types a query
- **THEN** an error message is displayed

#### Scenario: Retry after network recovery loads results
- **WHEN** MockWebServer first enqueues an error then a valid response, and the user taps Retry
- **THEN** the results from the valid response are displayed

### Requirement: Pagination loads the next page and appends results
`BookshelfIntegrationTest` SHALL verify that scrolling to the end of the first page triggers a second request and that both pages' items are visible.

#### Scenario: Second page appended after scrolling to bottom
- **WHEN** MockWebServer has page 1 (20 items) and page 2 (5 items) enqueued and the user scrolls to the bottom
- **THEN** items from both pages are visible in the list

### Requirement: Bookmarking a search result makes it appear on the Bookmarks tab
`BookshelfIntegrationTest` SHALL verify the cross-screen bookmark flow using `createAndroidComposeRule<MainActivity>()`.

#### Scenario: Bookmarked item appears on Bookmarks tab
- **WHEN** the user searches, taps the bookmark icon on a result, and switches to the Bookmarks tab
- **THEN** the bookmarked book's title is displayed on the Bookmarks screen

### Requirement: Unbookmarking removes the item from the Bookmarks tab
`BookshelfIntegrationTest` SHALL verify that toggling a bookmark off removes the item from the Bookmarks list.

#### Scenario: Unbookmarked item disappears from Bookmarks tab
- **WHEN** a book is bookmarked, the user navigates to Bookmarks, and taps the bookmark icon again
- **THEN** the book is no longer listed on the Bookmarks screen

### Requirement: Navigating between tabs preserves search state
`BookshelfIntegrationTest` SHALL verify that switching to the Bookmarks tab and back does not clear the Search results.

#### Scenario: Search results survive tab switch
- **WHEN** the user searches for a book, switches to Bookmarks, then switches back to Search
- **THEN** the previous search results are still displayed

### Requirement: Back navigation from BookDetailScreen returns to Search
`BookshelfIntegrationTest` SHALL verify that pressing Back from the detail screen returns to the Search screen with results intact.

#### Scenario: Back from detail restores Search results
- **WHEN** the user taps a search result to open BookDetailScreen and then presses the back button
- **THEN** the Search screen is displayed with the previous results visible

### Requirement: Navigating away from a loading BookDetailScreen does not crash
`BookshelfIntegrationTest` SHALL verify that back-navigating while the detail screen is still loading (network response pending) completes without a crash or ANR.

#### Scenario: No crash when leaving detail screen mid-load
- **WHEN** the user opens a book detail while MockWebServer has a delayed or no response enqueued and immediately presses Back
- **THEN** the app returns to the Search screen without crashing
