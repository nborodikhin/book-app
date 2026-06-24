## Context

The Bookshelf app has four comprehensive unit test suites (`SearchViewModelTest`, `BookDetailViewModelTest`, `BookmarksViewModelTest`, `BookRepositoryTest`) and four instrumentation test files (`SearchScreenTest`, `BookDetailScreenTest`, `BookmarksScreenTest`, `BookDaoTest`). Robolectric and Mockito are already on the test classpath. The app has no lint configuration block, so lint runs with Android defaults and no enforcement gate.

Several spec areas (`compose-previews`, `detail-bookmark-anchor`) describe behaviors that cannot be meaningfully covered by instrumented tests — Compose `@Preview` rendering is a Studio tool, and scroll-anchor layout correctness is not reliably testable with `createComposeRule`. These gaps need documenting in a manual runbook rather than forcing low-value tests.

## Goals / Non-Goals

**Goals:**
- Add a `lint {}` block to `app/build.gradle.kts` with `abortOnError = true` and a baseline for pre-existing issues
- Simplify ViewModel, repository, and screen code using `/simplify` without changing behavior
- Close any remaining unit test gaps (toggling bookmark from `SearchViewModel`, `BookDetailViewModel.onToggleBookmark`)
- Run the full test suite and lint; all must pass before archiving
- Produce `testing.md` — a structured manual checklist covering scenarios not amenable to automation
- Add a maintenance requirement to the affected specs stating tests and runbook must be kept in sync with implementation

**Non-Goals:**
- Adding new product features or changing user-visible behavior
- Achieving 100% line coverage (target is spec scenario coverage)
- Automating scroll-position tests or `@Preview` render verification

## Decisions

**Lint baseline**: Use `./gradlew lint` to generate `lint-baseline.xml`, then enable `abortOnError = true` only in the `release` buildType. This avoids blocking debug builds on pre-existing issues while catching regressions in CI.
- Alternative: `checkAllWarnings = true` globally — rejected as too noisy for a first pass.

**Simplify scope**: Apply `/simplify` to ViewModels and `BookRepository` only. Screen composables are intentionally kept verbose for readability and Compose tooling compatibility.

**Test gaps to fill**:
- `SearchViewModelTest` — add test for `isBookmarked` flow emitting `true` for a bookmarked workId
- `BookDetailViewModelTest` — add test that `onToggleBookmark(true)` calls `repository.setBookmarked` and `onToggleBookmark(false)` clears bookmark
- These use Mockito mocks already in place; no new dependencies needed.

**Manual runbook placement**: `testing.md` at the repo root, checked in and reviewed alongside spec changes. Format: Markdown checklist grouped by feature area.

**Spec maintenance note**: Rather than modifying every spec individually, add a single `ADDED Requirement: Test and runbook maintenance` to each affected spec's delta file. This keeps the requirement canonical in the spec rather than only in a README.

## Risks / Trade-offs

- [Lint baseline may hide real issues] → Baseline is regenerated on each significant change; file is committed so diffs are visible in PR review.
- [Simplification may introduce subtle bugs] → All simplifications must pass the existing test suite before committing.
- [Manual runbook becomes stale] → Mitigated by the spec maintenance requirement, which makes it a spec violation (not just a nice-to-have) to leave the runbook out of date.

## Migration Plan

1. Add lint block and generate baseline
2. Run `/simplify` on ViewModels and repository; run tests
3. Fill unit test gaps; run tests
4. Verify instrumentation tests pass on device/emulator
5. Write `testing.md`
6. Update delta specs with maintenance requirement
7. Run full `./gradlew lint test connectedAndroidTest`; all green before archiving
