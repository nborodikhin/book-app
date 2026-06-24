## Why

The manual testing runbook currently contains 13 items that require a real device and real network to verify. Most of these can be automated by replacing the live Open Library API with a local `MockWebServer` and running full-navigation instrumentation tests against `MainActivity`. This reduces pre-release manual effort to 2 items (cover image CDN loading, device rotation).

## What Changes

- Add `mockwebserver` to the test dependency catalog and `app/build.gradle.kts`
- Add a `FakeNetworkModule` (`@TestInstallIn`) that replaces `NetworkModule` in instrumentation tests, pointing Retrofit at a per-test `MockWebServer` instance
- Add a `FakeDataStoreModule` (`@TestInstallIn`) that replaces `DataStoreModule` with an isolated temp-file DataStore per test run, preventing state leakage between tests
- Add `BookshelfIntegrationTest` — a new instrumentation test class using `createAndroidComposeRule<MainActivity>()` — covering all cross-screen and network-dependent scenarios currently in the manual runbook
- Add two missing unit tests to `BookRepositoryTest`: (a) `getBookDetail` returns `null` when `api.getWork` throws; (b) `setBookmarked(true)` still calls `bookDao.insert` with fallback metadata when `api.getWork` throws — both required by the `coroutine-exception-handling` spec
- Update `testing.md`: remove newly automated items, leaving only 2 items in the manual checklist

## Capabilities

### New Capabilities
- `network-test-infra`: Hilt test modules (`FakeNetworkModule`, `FakeDataStoreModule`) and shared `MockWebServer` helpers that enable full-stack instrumentation tests without a real network
- `navigation-integration-tests`: Integration test class covering search → detail → bookmarks navigation flows, network error/retry, pagination, and cross-screen state transitions

### Modified Capabilities

- `manual-testing-runbook`: Runbook reduced to 2 genuine manual items; all newly automated scenarios removed from the checklist

## Impact

- `gradle/libs.versions.toml` — new `mockwebserver` entry under `[libraries]`
- `app/build.gradle.kts` — `androidTestImplementation(libs.mockwebserver)`
- `app/src/androidTest/` — new files:
  - `di/FakeNetworkModule.kt`
  - `di/FakeDataStoreModule.kt`
  - `utils/MockWebServerRule.kt` (JUnit rule wrapping MockWebServer lifecycle)
  - `ui/BookshelfIntegrationTest.kt`
- `app/src/test/java/com/example/bookapp/data/repository/BookRepositoryTest.kt` — 2 new unit tests for error branches in `getBookDetail` and `setBookmarked`
- `testing.md` — manual checklist trimmed to 2 items
