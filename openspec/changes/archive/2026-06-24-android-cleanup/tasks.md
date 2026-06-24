## 1. Lint Configuration

- [x] 1.1 Add `lint {}` block to `app/build.gradle.kts` with `abortOnError = true` scoped to release buildType and `baseline = file("lint-baseline.xml")`
- [x] 1.2 Run `./gradlew lint` to generate `lint-baseline.xml` and commit it
- [x] 1.3 Add a note to `CLAUDE.md` that `./gradlew lint` must pass before merging and explain the baseline approach

## 2. Code Simplification

- [x] 2.1 Run `/simplify` on `SearchViewModel`, `BookDetailViewModel`, `BookmarksViewModel`, and `BookRepository`; review and apply safe suggestions
- [x] 2.2 Run `./gradlew test` after simplification to confirm all unit tests still pass

## 3. Unit Test Gaps

- [x] 3.1 Add test to `BookDetailViewModelTest`: `onToggleBookmark(true)` calls `repository.setBookmarked(workId, true, ...)`
- [x] 3.2 Add test to `BookDetailViewModelTest`: `onToggleBookmark(false)` calls `repository.setBookmarked(workId, false)`
- [x] 3.3 Run `./gradlew test` and confirm all unit tests pass

## 4. Instrumentation Tests

- [x] 4.1 Run existing instrumentation tests (`./gradlew connectedAndroidTest`) on a device or emulator; document any failures
- [x] 4.2 Fix any failing instrumentation tests

## 5. Manual Testing Runbook

- [x] 5.1 Create `testing.md` at repo root with a checklist covering: end-to-end bookmark lifecycle (search → bookmark → bookmarks tab → add note → unbookmark), scroll-anchor bookmark icon on BookDetailScreen, configuration change (rotation) while typing a note, network error + Retry flow, and cover image loading
- [x] 5.2 Mark each item in the runbook as `[automated]` or `[manual]` to distinguish coverage

## 6. Spec Maintenance Notes

- [x] 6.1 Confirm all six delta spec files in `openspec/changes/android-cleanup/specs/` are complete and correctly formatted
- [x] 6.2 Commit all planning artifacts (proposal, design, specs, tasks)

## 7. Final Verification

- [x] 7.1 Run `./gradlew lint test` — all checks green
- [x] 7.2 Run `./gradlew connectedAndroidTest` — all instrumentation tests pass
- [x] 7.3 Run `/opsx:verify` to confirm implementation matches specs before archiving
