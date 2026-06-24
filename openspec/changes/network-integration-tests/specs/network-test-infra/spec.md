## ADDED Requirements

### Requirement: FakeNetworkModule replaces NetworkModule in instrumentation tests
A `FakeNetworkModule` annotated with `@Module @TestInstallIn(SingletonComponent::class)` and `@UninstallModules(NetworkModule::class)` SHALL provide a `Retrofit` instance whose base URL points at a `MockWebServer` running on localhost. The base URL SHALL be injectable via a `@BaseUrl`-qualified `String` binding so tests can enqueue responses without referencing the server port directly.

#### Scenario: Retrofit base URL points at MockWebServer in tests
- **WHEN** an instrumentation test annotated with `@HiltAndroidTest` runs
- **THEN** all HTTP calls made by `OpenLibraryApi` go to the local `MockWebServer` and NOT to `https://openlibrary.org/`

#### Scenario: Real NetworkModule is absent from the test graph
- **WHEN** `FakeNetworkModule` is present
- **THEN** no binding from `NetworkModule` is active; the `@UninstallModules` annotation ensures this

### Requirement: FakeDataStoreModule provides isolated DataStore per test run
A `FakeDataStoreModule` annotated with `@Module @TestInstallIn(SingletonComponent::class)` and `@UninstallModules(DataStoreModule::class)` SHALL provide a `DataStore<Preferences>` backed by a `TemporaryFolder`-managed file so no preference state leaks between test runs.

#### Scenario: DataStore state does not leak between tests
- **WHEN** two integration tests run sequentially and the first test writes a bookmark
- **THEN** the second test starts with an empty DataStore and does not see the bookmark written by the first test

#### Scenario: DataStore file is cleaned up after test run
- **WHEN** a test completes (pass or fail)
- **THEN** the temporary DataStore file is deleted and does not persist to the device

### Requirement: MockWebServerRule manages server lifecycle per test class
A `MockWebServerRule` JUnit `ExternalResource` rule SHALL start `MockWebServer` before the first test in a class and shut it down after the last, exposing `enqueueResponse(body: String, code: Int = 200)` and `enqueueError()` helpers.

#### Scenario: MockWebServer is running before each test
- **WHEN** a test class uses `MockWebServerRule` as a `@get:Rule`
- **THEN** `MockWebServer` is started and its URL is available before `@Before` setup runs

#### Scenario: enqueueError simulates a connection failure
- **WHEN** `enqueueError()` is called and the app makes an HTTP request
- **THEN** the request fails with an `IOException` (socket policy: disconnect after connect)
