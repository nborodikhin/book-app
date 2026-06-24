## ADDED Requirements

### Requirement: Lint is configured with a baseline and abortOnError across all build types
`app/build.gradle.kts` SHALL include a `lint {}` block that sets `baseline = file("lint-baseline.xml")` and `abortOnError = true` (applied globally to all build variants, because AGP does not support per-variant `abortOnError` in the `lint {}` block). A `lint-baseline.xml` SHALL be committed to the repo capturing any pre-existing lint issues at the time of first configuration.

#### Scenario: Lint passes on clean baseline
- **WHEN** `./gradlew lint` is run with no code changes after baseline generation
- **THEN** the lint task exits with code 0 and reports no new issues above the baseline

#### Scenario: New lint issue causes lint to fail
- **WHEN** a new lint violation is introduced in production code and `./gradlew lint` is run
- **THEN** the lint task exits with a non-zero code and reports the new violation

#### Scenario: Baseline suppresses pre-existing issues
- **WHEN** `./gradlew lint` is run and a pre-existing issue recorded in the baseline is encountered
- **THEN** that issue is suppressed and does not cause a build failure

### Requirement: Lint check is documented as a required step before merging
The project README or CLAUDE.md SHALL note that `./gradlew lint` must pass before a pull request is merged, referencing the baseline approach for handling pre-existing issues.

#### Scenario: Lint step is visible in project instructions
- **WHEN** a developer reads CLAUDE.md or README.md
- **THEN** they can find a clear instruction to run lint and an explanation that `lint-baseline.xml` captures pre-existing suppressions
