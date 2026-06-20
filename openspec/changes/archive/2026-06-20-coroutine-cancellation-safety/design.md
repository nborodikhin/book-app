## Context

Kotlin coroutines use `CancellationException` (a subclass of `IllegalStateException`, itself a subclass of `Exception`) to propagate cancellation cooperatively. Any `catch (e: Exception)` block in a suspend function will intercept it, preventing the coroutine from terminating. All three affected sites have intentional fallback logic that is correct for genuine errors — the fix is minimal: rethrow `CancellationException` before that logic runs.

## Goals / Non-Goals

**Goals:**
- Make all three catch sites cancellation-safe with minimal change
- Document the pattern in CLAUDE.md so future code doesn't reintroduce it

**Non-Goals:**
- Switching to `runCatching` or other error-handling abstractions
- Auditing test code (unit tests don't cancel coroutines mid-execution)
- Adding retry logic or other error-handling improvements beyond this fix

## Decisions

**Use a leading `catch (e: CancellationException) { throw e }` block**: This is the most explicit and readable form. The alternative — `if (e is CancellationException) throw e` inside the existing catch — works but buries the safety check. A separate rethrow block makes the intent obvious and matches the idiom most Kotlin teams recognise.

**Import `kotlinx.coroutines.CancellationException`**: Both files already import from `kotlinx.coroutines`, so no new dependency is needed.

**CLAUDE.md addition**: A short "Kotlin Coroutines" section listing the single rule: never use `catch (e: Exception)` in a suspend function without rethrowing `CancellationException`. Include the correct pattern inline so it's copy-paste ready.

## Risks / Trade-offs

- [Risk] Rethrow changes observable behaviour if a caller was (incorrectly) relying on the swallowed cancellation — Mitigation: no caller does this; all three sites are called from `viewModelScope.launch` which handles cancellation at the scope level
- [Risk] Missing a fourth catch site added in the future — Mitigation: CLAUDE.md rule + the spec make this a standing requirement, not a one-off fix
