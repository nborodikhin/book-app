# Bookshelf — Manual Testing Runbook

Items that cannot be verified by the automated test suite. Run before each release.

Automated checks: `./gradlew lint test connectedAndroidTest`

---

- [ ] **Cover image loading** — Open a book that has a cover image; confirm the cover loads from the Open Library CDN. (Cannot be automated: requires real external URL and Coil network stack.)
- [ ] **Device rotation with note open** — Rotate the device while the note field is open on BookDetailScreen; confirm the note text is preserved after rotation. (Not automated: `ActivityScenario.recreate()` is flaky in CI.)
