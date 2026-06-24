## Context

The existing instrumentation tests all use `createComposeRule()` with stateless content overloads — deliberately no Hilt, no ViewModel, no network. That pattern is fast and reliable for UI-shape tests, but it cannot cover cross-screen flows or real data paths.

The new integration tests need three things the existing tests lack: a fake network layer, isolated persistent storage, and a live navigation host. `MockWebServer` provides the network layer; a temp-dir `DataStore` provides isolation; and `createAndroidComposeRule<MainActivity>()` provides the navigation host with real ViewModels and Hilt injection.

The app's Hilt graph has two singleton modules that touch external state: `NetworkModule` (hardcodes `https://openlibrary.org/`) and `DataStoreModule` (reads a named `DataStore` tied to the app's package). Both must be replaced in tests.

## Goals / Non-Goals

**Goals:**
- Provide `FakeNetworkModule` and `FakeDataStoreModule` as shared test infrastructure usable by any future integration test class
- Cover every manual runbook scenario that is network- or navigation-dependent
- Keep integration tests isolated: no shared DataStore state, MockWebServer reset between tests
- Leave the existing stateless screen tests untouched

**Non-Goals:**
- Testing Coil cover image loading from the CDN (requires real network; stays manual)
- Testing device rotation (flaky with `ActivityScenario.recreate()`; stays manual)
- Replacing unit tests — integration tests complement, not duplicate, them

## Decisions

**MockWebServer scope: JUnit Rule per test class, not per test method.**
MockWebServer starts/stops around each test class via a custom `MockWebServerRule`. Individual tests enqueue responses at the start of each `@Test`. This keeps startup overhead low (one server per class) while ensuring each test controls its own response queue.

**Retrofit base URL injection via qualifier.**
`NetworkModule` will expose its base URL as a `@BaseUrl String` binding so `FakeNetworkModule` can override just the URL without duplicating the JSON/converter configuration. Alternative: override the entire `Retrofit` binding — rejected because it duplicates the serializer setup and breaks if `NetworkModule` changes serializer config.

**DataStore isolation: temp-file DataStore via `@TestInstallIn`.**
`FakeDataStoreModule` uninstalls `DataStoreModule` and provides a `PreferenceDataStoreFactory`-backed DataStore writing to a `TemporaryFolder` path. The `TemporaryFolder` is owned by the test class and deleted after each test run. Same pattern already proven in `BookRepositoryTest`.

**Single integration test class for all flows.**
All cross-screen and network-dependent scenarios live in `BookshelfIntegrationTest`. If the class grows beyond ~20 tests it can be split by feature area, but a single class is simpler to start and shares the MockWebServer + DataStore setup.

**Fake JSON responses: hand-written inline strings, not fixture files.**
Inline JSON strings in each test make the test self-documenting and avoid a fixtures directory that can drift out of sync with the API model. Responses are minimal — only the fields the app actually reads.

## Risks / Trade-offs

- [MockWebServer port conflicts in parallel test execution] → MockWebServer binds to port 0 (OS-assigned), so parallel test shards won't collide.
- [DataStore temp files not cleaned up on test crash] → `TemporaryFolder` JUnit rule handles cleanup even on failure.
- [Integration tests are slower than unit tests] → Expected; these replace manual verification, not unit tests. Target: full suite under 30 seconds.
- [MainActivity starts a real coroutine on launch] → `StandardTestDispatcher` is NOT used here — the app runs on real dispatchers with `IdlingResource` or `waitForIdle()` to synchronize. Compose test rule's built-in idle waiting covers most cases.
