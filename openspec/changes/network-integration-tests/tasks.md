## 1. Dependencies

- [x] 1.1 Add `mockwebserver` to `gradle/libs.versions.toml` under `[libraries]`: `mockwebserver = { group = "com.squareup.okhttp3", name = "mockwebserver", version.ref = "okhttp" }`
- [x] 1.2 Add `androidTestImplementation(libs.mockwebserver)` to `app/build.gradle.kts`

## 2. NetworkModule Refactor

- [x] 2.1 Extract base URL from `NetworkModule` into a `@BaseUrl`-qualified `String` binding so the URL can be overridden independently of the Retrofit/serializer configuration
- [x] 2.2 Update `NetworkModule.provideRetrofit` to accept `@BaseUrl baseUrl: String` instead of hardcoding `"https://openlibrary.org/"`

## 3. Test Infrastructure

- [x] 3.1 Create `app/src/androidTest/java/com/example/bookapp/di/FakeNetworkModule.kt` — `@TestInstallIn` module providing Retrofit pointed at `MockWebServer` localhost URL
- [x] 3.2 Create `app/src/androidTest/java/com/example/bookapp/di/FakeDataStoreModule.kt` — `@TestInstallIn` module providing a temp-file `DataStore<Preferences>` using `PreferenceDataStoreFactory`
- [x] 3.3 Create `app/src/androidTest/java/com/example/bookapp/utils/MockWebServerRule.kt` — JUnit `ExternalResource` rule that starts/stops `MockWebServer` and exposes `enqueueResponse(body, code)` and `enqueueError()` helpers

## 4. Integration Test Class

- [x] 4.1 Create `app/src/androidTest/java/com/example/bookapp/ui/BookshelfIntegrationTest.kt` with `@HiltAndroidTest`, `createAndroidComposeRule<MainActivity>()`, and the `MockWebServerRule`
- [x] 4.2 Add test: search results appear from MockWebServer response
- [x] 4.3 Add test: network error during search shows error message
- [x] 4.4 Add test: Retry after error loads results from second enqueued response
- [x] 4.5 Add test: pagination — second page appended after scrolling to bottom
- [x] 4.6 Add test: bookmark from Search → Bookmarks tab shows the bookmarked book
- [x] 4.7 Add test: unbookmark from Bookmarks tab → book disappears
- [x] 4.8 Add test: tab switch Search → Bookmarks → Search preserves results
- [x] 4.9 Add test: Back from BookDetailScreen returns to Search with results
- [x] 4.10 Add test: navigate away while detail is loading — no crash

## 5. Update testing.md

- [x] 5.1 Trim `testing.md` to exactly two manual items: cover image loading and device rotation

## 6. Verify

- [x] 6.1 Run `./gradlew test` — unit tests still pass
- [ ] 6.2 Run `./gradlew connectedAndroidTest` — all tests pass including new integration tests
- [ ] 6.3 Run `./gradlew lint` — no new lint issues
