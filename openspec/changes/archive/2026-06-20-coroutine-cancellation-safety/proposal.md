## Why

All three `catch (e: Exception)` blocks in production suspend functions also catch `CancellationException`, the mechanism Kotlin coroutines use to signal cooperative cancellation. This means a cancelled coroutine can silently set error UI state or fall back to stale data instead of terminating cleanly. Discovered during code review; fixing it now before adding more coroutine-heavy features.

## What Changes

- `SearchViewModel.fetchPage()` — rethrow `CancellationException` before the existing error-state logic
- `BookRepository.getBookDetail()` — rethrow `CancellationException` before returning null
- `BookRepository.setBookmarked()` — rethrow `CancellationException` before the local-fallback path
- `CLAUDE.md` — add a **Kotlin Coroutines** section documenting this pattern so it's caught in future code

## Capabilities

### New Capabilities

- `coroutine-exception-handling`: Rule that suspend functions must not swallow `CancellationException`

### Modified Capabilities

<!-- None — no existing spec-level behavior changes -->

## Impact

- `app/src/main/java/com/example/bookapp/ui/search/SearchViewModel.kt`
- `app/src/main/java/com/example/bookapp/data/repository/BookRepository.kt`
- `CLAUDE.md`
- No external API or dependency changes; no behavioral change for non-cancelled coroutines
