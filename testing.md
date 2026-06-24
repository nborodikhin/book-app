# Bookshelf — Manual Testing Runbook

Items that cannot be verified by the automated test suite. Run before each release.

Automated checks: `./gradlew lint test connectedAndroidTest`

---

## Search Screen

- [ ] Type a query (e.g. "dune") — results appear from Open Library
- [ ] Scroll to the bottom of a long result list — next page loads without a gap or duplicate
- [ ] Enable airplane mode mid-search — error message appears
- [ ] Tap Retry after re-enabling network — results load correctly

## Book Detail Screen

- [ ] Open a book that has a cover — cover image loads from the CDN
- [ ] Bookmark a book, then scroll down — bookmark icon stays visible at the top-end corner (does not scroll off)
- [ ] Rotate the device while the note field is open — note text is preserved after rotation

## Bookmarks Screen

- [ ] Bookmark a book from Search → switch to Bookmarks tab — book appears immediately
- [ ] Unbookmark from the Bookmarks list — book disappears from the list
- [ ] Unbookmark from BookDetailScreen — book also disappears from Bookmarks tab

## End-to-End: Bookmark Lifecycle

- [ ] Search → bookmark a book → Bookmarks tab: book is listed
- [ ] Open the bookmarked book → type a note → navigate back → return to the detail screen: note is shown
- [ ] Unbookmark the book → re-open it: note field is empty

## Navigation

- [ ] Switch between Search and Bookmarks tabs repeatedly — no state lost
- [ ] Open a book detail → press Back — return to Search with results intact
- [ ] Navigate away from BookDetailScreen while it is still loading — no crash or ANR
