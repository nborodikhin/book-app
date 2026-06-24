## Why

The codebase has grown through several feature changes but lacks a lint target, has simplification opportunities across business logic, and several specs (bookmarks-filter, note-edit-state, detail-bookmark-anchor, compose-previews) lack corresponding automated tests. A cleanup pass will enforce quality gates, improve maintainability, and create a durable testing baseline with a clear manual runbook for what cannot be automated.

## What Changes

- Add a `lint` Gradle task configuration with baseline and failure rules
- Apply code simplifications across ViewModels, repository, and UI components (via `/simplify`)
- Run compliance checks (lint, detekt if applicable) and report findings
- Add missing unit tests for `BookmarksViewModel` filter logic, `BookDetailViewModel` note-edit state, and `SearchViewModel` pagination edge cases using Robolectric and Mockito
- Expand instrumentation tests for `BookmarksScreen` (filter UI) and `BookDetailScreen` (note-edit state scenarios per spec)
- Run the full test suite and lint; fix all failures
- Document what cannot be covered by automated tests in `testing.md` as a manual runbook
- Add a spec note to all affected specs that tests and the runbook must be kept in sync

## Capabilities

### New Capabilities
- `android-lint-config`: Lint target configuration, baseline, and CI-ready failure rules
- `manual-testing-runbook`: `testing.md` with a structured checklist of scenarios not covered by automation

### Modified Capabilities
- `stateless-screen-tests`: Expand instrumented tests to cover note-edit-state and bookmarks-filter scenarios (spec compliance gap)
- `bookmarks-filter`: Add unit tests for filter logic in `BookmarksViewModel` (currently untested)
- `note-edit-state`: Add unit tests for one-shot initialization, disable-before-init, and 300-char cap behaviors
- `coroutine-exception-handling`: Spec note added — test suite and runbook must be maintained alongside implementation

## Impact

- `app/build.gradle.kts` — lint block added
- `lint-baseline.xml` — new file (generated)
- `app/src/test/` — new/expanded unit tests: `BookmarksViewModelTest`, `BookDetailViewModelTest`, `SearchViewModelTest`
- `app/src/androidTest/` — expanded: `BookmarksScreenTest`, `BookDetailScreenTest`
- `testing.md` — new file at repo root
- `openspec/specs/*/spec.md` — maintenance notes added to: `bookmarks-filter`, `note-edit-state`, `stateless-screen-tests`, `coroutine-exception-handling`, `android-lint-config` (new), `manual-testing-runbook` (new)
