# Status

The coding agent updates this file at the end of its run.

| Spec | Status | Notes |
|---|---|---|
| 01 — Redmine-style shell and theme | done | `MainLayout` is now a plain `Div` + `RouterLayout`: dark top bar, blue header, tab row (anchors with `router-link`, the current tab is highlighted by longest path prefix). `AdminView` at `/admin`. Styles in `styles.css` (Lumo custom properties, `vaadin-grid::part(...)` selectors). "Overview" tab points to the project list (`it/projects`). |
| 02 — Demo data | done | `demo/DemoDataSeeder` (`ApplicationRunner`, skipped when any project exists). Users are created through User Manager's `UserService`; Issue Tracker mirrors them via `UserCreatedEvent`. Issue Tracker ships without trackers, statuses, priorities, activities or member roles, so the seeder creates them (Bug/Feature/Support; New/In Progress/Resolved/Closed; Low–Urgent; Development/Design; Manager/Developer/Reporter) plus workflows (all transitions for Manager and Developer). Two projects, 6 memberships, 3 open + 4 closed issues, 4 time entries. Only three issues are open so a new user can still create two before hitting the free-mode limit of five open issues. Open: website Bug/High → alice, website Feature/Normal → bob, mobile Support/Low → alice; closed: footer link, reset-password email, push notifications, login crash. Time entries are logged only on open issues. Verified: `2 projects, 7 issues (3 open), 4 time entries, 4 Issue Tracker users`, no seeder warnings. |
| 03 — Home page | done | Welcome text, "Latest projects" box (from `ProjectService.getProjectsAvailableForLoggedInUser()`, linking to `it/projects/<identifier>/issues`), demo credentials box; flex boxes wrap on narrow screens. |
| 04 — Test findings | done | Grid columns come from Issue Tracker's global settings (Administration > Settings: "Issues list defaults" on the Issue tracking tab, "Projects list defaults" on the Projects tab), which start empty. `DemoDataSeeder.listDefaults()` fills them through `SettingsService.findSettings(...)`/`saveTabSettings(...)` with `IssuesTabDto` (tracker, status, priority, subject, assigned_to, updated_on) and `ProjectTabDto` (name, short_description, identifier); values are the lower-case `IssueColumn`/`ProjectColumn` names. It runs on every start but only writes a list while it is empty, so changes made in Settings are kept. "Administration" in `MainLayout` now also requires the Issue Tracker admin flag (`UserService.findLoggedInUserOptional()` → `UserDto.isAdmin()`); the seeder warns if `admin` lacks that flag (it does not). |

Verification: `mvn -q -DskipTests package` passes; `mvn spring-boot:run` boots with no errors in the log,
`/` redirects to `/login`, `/login` returns 200. After spec 04 the seeder logs no warnings (column
defaults saved, `admin` is an Issue Tracker administrator). The logged-in UI was **not** exercised
in a browser during this run, so the visual checks in the acceptance criteria are still open.

## Not done
- **Browser check of spec 04**: the acceptance criteria (issue/project grid columns as `admin`, no
  "Administration" link for `alice`) were not checked in a browser. Grid striping (`odd-row-cell`
  part) from spec 01 is also still unchecked.
- **Admin links on `/admin` for non-admins**: only the top-bar link is hidden. A non-admin who types
  `/admin` still sees the page; the Issue Tracker links on it redirect to `/` with an access-denied
  notification (`AdminAccessGuard`).
- **User Manager roles for demo users**: User Manager only ships `ADMIN` and `ANONYMOUS` authorities,
  so `alice`, `bob` and `carol` have no User Manager authority. Their project roles exist only in
  Issue Tracker, which is enough to log in and work on issues.
- **Profile data / emails**: the demo users have first and last names in Issue Tracker but no email
  addresses (spec 02 also excludes notifications).
