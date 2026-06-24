# Bookshelf — Testing Guide

This document describes how to verify the Bookshelf app. Each item is marked:
- `[automated]` — covered by a unit or instrumentation test; run `./gradlew test connectedAndroidTest` to verify
- `[manual]` — must be verified by hand on a device or emulator

Keep this runbook in sync with implementation. When a scenario changes, update the relevant checklist item in the same PR.

---

## Automated Test Suite

```bash
# Unit tests (JVM, fast)
./gradlew test

# Instrumentation tests (requires connected device or emulator)
./gradlew connectedAndroidTest

# Lint (must pass before merging)
./gradlew lint
```

---

## Search

- `[automated]` Loading spinner visible while search is in progress
- `[automated]` Error message displayed when network request fails
- `[automated]` Retry button appears in error state and triggers a new search
- `[automated]` Results list shown on success with correct titles and authors
- `[automated]` Bookmark icon is filled for bookmarked results, outline for non-bookmarked
- `[automated]` Pagination: next page appended when scrolled to bottom
- `[automated]` Pagination error shows inline banner with Retry button
- `[automated]` Rapid typing triggers only one network request (debounce)
- `[manual]` Type a real query (e.g. "dune") — confirm results appear from Open Library
- `[manual]` Scroll to the bottom of a long result list — confirm next page loads seamlessly
- `[manual]` Disable airplane mode mid-search — confirm error message appears
- `[manual]` Re-enable network and tap Retry — confirm results load correctly

---

## Book Detail

- `[automated]` Loading state: progress indicator shown, no book content
- `[automated]` Error state: error message shown, no book content
- `[automated]` Success state: book title, authors, synopsis rendered
- `[automated]` Note field visible only when book is bookmarked
- `[automated]` Note field hidden when book is not bookmarked
- `[automated]` Character counter shows `0/300` on empty note
- `[automated]` Character counter updates as user types
- `[automated]` Input capped at 300 characters in the UI layer
- `[automated]` `onNoteChange` truncates to 300 chars before persisting
- `[automated]` Note initializes from the first ViewModel emission (null sentinel → real value)
- `[automated]` toggleBookmark(true) calls repository.setBookmarked with bookmarked=true
- `[automated]` toggleBookmark(false) calls repository.setBookmarked with bookmarked=false
- `[manual]` Tap the bookmark icon — confirm it toggles between filled and outline states
- `[manual]` Bookmark a book, type a note, then scroll down — confirm the bookmark icon remains visible at the top-end corner (not scrolled off screen)
- `[manual]` Bookmark a book, type a note, then rotate the device — confirm the note text is preserved after rotation
- `[manual]` Navigate back from BookDetailScreen while a note is unsaved — confirm the note is retained when returning to the screen
- `[manual]` Open a book that has a cover image — confirm the cover loads from the Open Library CDN

---

## Bookmarks

- `[automated]` Empty state message shown when no books are bookmarked
- `[automated]` List items rendered when bookmarks exist
- `[automated]` Filter field visible on bookmarks screen
- `[automated]` Typing in the filter narrows the list by title (case-insensitive)
- `[automated]` Filter also matches by author (case-insensitive)
- `[automated]` "No results" message when filter matches nothing
- `[automated]` Clearing filter restores all bookmarks
- `[manual]` Bookmark a book from Search — confirm it appears in Bookmarks tab immediately
- `[manual]` Unbookmark a book from the Bookmarks tab — confirm it disappears from the list
- `[manual]` Unbookmark from BookDetailScreen — confirm the book also disappears from Bookmarks tab

---

## End-to-End Bookmark Lifecycle

- `[manual]` Search for a book → tap the bookmark icon → switch to Bookmarks tab → confirm the book appears
- `[manual]` Tap the bookmarked item to open it → type a note → navigate back → return to the detail screen → confirm the note was saved
- `[manual]` From Bookmarks, unbookmark the book → confirm it is removed from the list and its note is cleared (tap it again and verify the note field is empty)

---

## Note Persistence and Initialization

- `[automated]` Note field is disabled until the first ViewModel emission is received
- `[manual]` Open a bookmarked book with an existing note — confirm the note field is pre-populated correctly
- `[manual]` Rotate the device while typing a note — confirm the field is not reset to empty

---

## Navigation

- `[manual]` Tap Search tab → Bookmarks tab → Search tab — confirm no state is lost
- `[manual]` Tap a search result, then press the system Back button — confirm return to Search with results intact
- `[manual]` Deep-link directly to a BookDetailScreen — confirm the screen loads correctly

---

## Coroutine Cancellation Safety

- `[automated]` Error branches in suspend functions are caught without swallowing CancellationException
- `[manual]` Navigate away from BookDetailScreen while it is loading — confirm no crash or ANR occurs (cooperative cancellation working)

---

## Lint

- `[automated]` `./gradlew lint` reports no new issues above the baseline (`lint-baseline.xml`)
- `[manual]` After any change that introduces a new suppress annotation, confirm a justification comment is present inline
