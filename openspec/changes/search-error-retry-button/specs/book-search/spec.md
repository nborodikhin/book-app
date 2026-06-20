## MODIFIED Requirements

### Requirement: Error state
If a search request fails, the app SHALL display an error icon, a message, and a **Retry** button. Tapping the button SHALL re-execute the same search query without requiring the user to re-type it.

#### Scenario: Network error during initial search
- **WHEN** an initial search request (page 1) fails due to a network error
- **THEN** a full-screen error icon, "Something went wrong." message, and a "Retry" button are displayed

#### Scenario: Retry after initial search error
- **WHEN** the user taps the Retry button on the full-screen error state
- **THEN** the app re-executes the current search query from page 1

## ADDED Requirements

### Requirement: Pagination error state
If a pagination request (page > 1) fails, the app SHALL display an inline error banner at the bottom of the existing results list with a **Retry** button. The already-loaded results SHALL remain visible.

#### Scenario: Network error during pagination
- **WHEN** a request to load the next page fails due to a network error
- **THEN** the existing results remain visible and an inline banner appears at the bottom of the list with the text "Failed to load more." and a separate "Retry" button

#### Scenario: Retry after pagination error
- **WHEN** the user taps the Retry button in the pagination error banner
- **THEN** the app re-attempts to fetch the next page and the inline error banner is replaced by the pagination loading spinner
