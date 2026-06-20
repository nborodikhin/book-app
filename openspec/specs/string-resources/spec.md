# String Resources

## Purpose

Defines requirements for how user-visible strings are managed in the app. All user-facing text must be declared in Android string resource files rather than hardcoded in source files, ensuring consistency, maintainability, and localization readiness.

## Requirements

### Requirement: All user-visible strings defined in string resources
The app SHALL define every user-visible string in `app/src/main/res/values/strings.xml`. No user-visible string literals SHALL appear in Kotlin UI source files or ViewModels.

#### Scenario: UI strings loaded from resources
- **WHEN** a Compose screen renders any label, placeholder, error message, empty-state text, or content description
- **THEN** the string is sourced from `stringResource()` referencing a key in `strings.xml`

#### Scenario: ViewModel error strings loaded from resources
- **WHEN** a ViewModel emits an error state containing a user-visible message
- **THEN** the message string is sourced from `context.getString()` referencing a key in `strings.xml`

### Requirement: String resource keys follow a consistent naming convention
String keys SHALL follow the pattern `<screen>_<element>_<role>` (e.g. `search_placeholder`, `detail_back_button`, `bookmarks_empty_no_results`).

#### Scenario: Key naming is consistent across screens
- **WHEN** a developer adds a new string resource
- **THEN** the key name clearly identifies the screen, UI element, and purpose without ambiguity

### Requirement: Format-argument strings use Android placeholder syntax
Strings containing dynamic values (e.g. book title, filter query) SHALL use Android `%s` / `%1$s` format arguments in `strings.xml` and pass arguments at the call site via `stringResource(id, arg)` or `getString(id, arg)`.

#### Scenario: Content description with dynamic book title
- **WHEN** a book cover image is displayed
- **THEN** its content description is formed by substituting the book title into a string resource format argument

#### Scenario: Empty-state message with dynamic filter query
- **WHEN** the bookmarks filter returns no results
- **THEN** the empty-state message includes the current filter query substituted into a string resource format argument
