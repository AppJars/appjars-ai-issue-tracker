# Status

The coding agent updates this file at the end of its run.

| Spec | Status | Notes |
|---|---|---|
| 01 — Redmine-style shell and theme | done | `MainLayout` is now a plain `Div` + `RouterLayout`: dark top bar, blue header, tab row (anchors with `router-link`, the current tab is highlighted by longest path prefix). `AdminView` at `/admin`. Styles in `styles.css` (Lumo custom properties, `vaadin-grid::part(...)` selectors). "Overview" tab points to the project list (`it/projects`). |
| 02 — Demo data | done | `demo/DemoDataSeeder` (`ApplicationRunner`, skipped when any project exists). Users are created through User Manager's `UserService`; Issue Tracker mirrors them via `UserCreatedEvent`. Issue Tracker ships without trackers, statuses, priorities, activities or member roles, so the seeder creates them (Bug/Feature/Support; New/In Progress/Resolved/Closed; Low–Urgent; Development/Design; Manager/Developer/Reporter) plus workflows (all transitions for Manager and Developer). Two projects, 6 memberships, 5 open + 2 closed issues (closed ones are created first because free mode refuses any new issue once 5 are open), 4 time entries. Verified in free mode (empty license folder): `2 projects, 7 issues (5 open), 4 time entries, 4 Issue Tracker users`, no warnings. |
| 03 — Home page | done | Welcome text, "Latest projects" box (from `ProjectService.getProjectsAvailableForLoggedInUser()`, linking to `it/projects/<identifier>/issues`), demo credentials box; flex boxes wrap on narrow screens. |

Verification: `mvn -q -DskipTests package` passes; `mvn spring-boot:run` boots with no errors in the log,
`/` redirects to `/login`, `/login` and `/styles.css` return 200. The logged-in UI was **not** exercised
in a browser during this run, so the visual checks in the acceptance criteria are still open.

## Not done
- **Browser check of the logged-in pages**: tabs, admin links and grid styling were not checked visually.
  Grid striping relies on the `odd-row-cell` part; if the grid doesn't look striped, check that part's name in Vaadin 25.
- **Issue Tracker admin flag for links**: Issue Tracker guards its admin views with its own
  `AdminAccessGuard` (based on the Issue Tracker user's admin flag), not with access annotations.
  `canAccess` only checks the annotations, so a non-admin user like `alice` still sees the
  "Administration" link and the Issue Tracker admin links. Opening one of them redirects to `/`
  with an access-denied notification.
- **User Manager roles for demo users**: User Manager only ships `ADMIN` and `ANONYMOUS` authorities,
  so `alice`, `bob` and `carol` have no User Manager authority. Their project roles exist only in
  Issue Tracker, which is enough to log in and work on issues.
- **Profile data / emails**: the demo users have first and last names in Issue Tracker but no email
  addresses (spec 02 also excludes notifications).
