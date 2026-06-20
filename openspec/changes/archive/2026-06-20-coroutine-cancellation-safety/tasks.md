## 1. Fix catch blocks in production code

- [x] 1.1 Add `catch (e: CancellationException) { throw e }` before `catch (e: Exception)` in `SearchViewModel.fetchPage()` (line 104)
- [x] 1.2 Add `catch (e: CancellationException) { throw e }` before `catch (e: Exception)` in `BookRepository.getBookDetail()` (line 64)
- [x] 1.3 Add `catch (e: CancellationException) { throw e }` before `catch (e: Exception)` in `BookRepository.setBookmarked()` (line 94)
- [x] 1.4 Add `import kotlinx.coroutines.CancellationException` to both files if not already present

## 2. Update CLAUDE.md

- [x] 2.1 Add a "Kotlin Coroutines" section to `CLAUDE.md` with the rule and correct code pattern

## 3. Verify

- [x] 3.1 Run `grep -rn "catch (e: Exception)" app/src/main/java` and confirm no unguarded catches remain in production suspend functions
- [x] 3.2 Build and run unit tests to confirm no regressions
