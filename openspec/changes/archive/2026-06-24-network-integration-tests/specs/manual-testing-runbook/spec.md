## MODIFIED Requirements

### Requirement: A manual testing runbook exists at testing.md
The repository SHALL contain a `testing.md` file at the root that documents, as a structured Markdown checklist, all user-visible scenarios that are not covered by automated unit or instrumentation tests. The runbook SHALL be organized by feature area and SHALL clearly distinguish manual-only steps from steps that have automated coverage.

After this change, the runbook SHALL contain exactly the following two manual items and no others:

1. **Cover image loading** — Open a book that has a cover image; confirm the cover loads from the Open Library CDN. (Cannot be automated: requires real external URL and Coil network stack.)
2. **Device rotation with note open** — Rotate the device while the note field is open on BookDetailScreen; confirm the note text is preserved after rotation. (Not automated: `ActivityScenario.recreate()` is flaky in CI.)

All other scenarios previously listed as manual SHALL be covered by `BookshelfIntegrationTest` and removed from the runbook.

#### Scenario: testing.md contains exactly two manual items after this change
- **WHEN** a developer reads `testing.md` after this change is applied
- **THEN** the manual checklist contains exactly two unchecked items: cover image loading and device rotation

#### Scenario: Newly automated scenarios are not listed as manual
- **WHEN** a developer reads `testing.md`
- **THEN** search error/retry, pagination, bookmark lifecycle, tab switching, and back navigation are NOT listed as manual items
