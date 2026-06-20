## ADDED Requirements

### Requirement: Empty search results state
When a search completes successfully but returns no matching books, the app SHALL display a "No results" message in the body of the Search tab. The message SHALL be shown instead of an empty list.

#### Scenario: Search returns no results
- **WHEN** a search request completes and the API returns zero results
- **THEN** a "No results" message is displayed in the page body instead of an empty list
