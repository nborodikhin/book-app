## Context

The app currently has only `app_name` in `strings.xml`. All other user-visible copy — labels, placeholders, error messages, content descriptions, empty states — is inline in Compose UI code. This is a pure structural refactor with no behavioral changes.

## Goals / Non-Goals

**Goals:**
- Move every user-visible string out of Kotlin files and into `strings.xml`
- Use `stringResource()` in Compose UI and `context.getString()` in ViewModels
- Establish a consistent naming convention for string keys

**Non-Goals:**
- Adding translations or locale-specific `strings.xml` files
- Changing any displayed text or UX behavior
- Extracting strings used only in preview/test data (fake titles like "Dune", "Foundation")

## Decisions

**String key naming convention**: `<screen>_<element>_<role>`, e.g. `search_placeholder`, `detail_error_message`, `bookmarks_empty_no_results`. Flat namespace within one file is sufficient for this app's scale.

**ViewModel error strings**: ViewModels currently embed string literals directly (e.g. `SearchViewModel`, `BookDetailViewModel`). These will be moved to `strings.xml` and passed in via `Context` using `context.getString()`. This is simpler than injecting a string provider abstraction for an app of this size.

**Content descriptions with format args**: Strings like `"Cover of $title"` and `"No results for \"$filterQuery\""` will use Android string format args (`%s`) and `stringResource(id, arg)` / `getString(id, arg)` at call sites.

**Preview data strings**: Hardcoded book titles/authors in `@Preview` composables are test fixture data, not user-visible copy — they stay as literals.

## Risks / Trade-offs

- [Risk] ViewModel coupling to `Context` — Mitigation: already acceptable pattern for this app; no DI framework overhead warranted
- [Risk] Missing a string — Mitigation: tasks enumerate every affected file explicitly; grep verification step included
