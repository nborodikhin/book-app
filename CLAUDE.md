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

## Commits

Create a git commit after every completed task. Keep commit messages concise and focused on what the task accomplished.
