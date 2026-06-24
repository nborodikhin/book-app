# Bookshelf — Claude Guidelines

## Android Development

Always use the `android-cli` skill for anything Android-related:

- **Documentation** — `android docs search "<keywords>"` before answering questions about Android APIs, libraries, or best practices
- **Running the app** — `android run` to deploy and test on a device or emulator
- **Emulator management** — `android emulator create/start/stop/list`
- **SDK management** — `android sdk install/update/list`
- **UI inspection** — `android layout` to inspect Compose UI trees when debugging layout issues
- **Screenshots** — `android screen capture` to capture device state

Do not rely on training-data knowledge for Android APIs — always fetch current docs via the skill.

## OpenSpec Workflow

Use OpenSpec skills to plan and track all feature work:

- **Propose** — `/opsx:propose` to create a new change with proposal, design, specs, and tasks
- **Implement** — `/opsx:apply` to work through tasks one by one
- **Verify** — `/opsx:verify` before archiving to confirm implementation matches specs
- **Archive** — `/opsx:archive` to finalize a completed change

All feature changes should go through OpenSpec before implementation begins.

## Kotlin Coroutines

Never use `catch (e: Exception)` alone in a suspend function — it swallows `CancellationException` and breaks cooperative cancellation. Always add a rethrow guard first:

```kotlin
} catch (e: CancellationException) {
    throw e
} catch (e: Exception) {
    // handle genuine errors
}
```

This applies to every `try/catch` inside a `suspend fun`, regardless of whether the catch has intentional fallback logic.

## Lint

Run `./gradlew lint` before merging any change. The project uses a committed `app/lint-baseline.xml` to suppress pre-existing warnings — only new violations above the baseline will fail the build. To update the baseline after intentionally fixing or introducing a suppressed issue, delete `lint-baseline.xml` and re-run lint to regenerate it.

## Commits

Create a git commit after every completed task. Keep commit messages concise and focused on what the task accomplished.
