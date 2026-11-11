# 04 — Findings from the first manual test

Found by logging in as `admin` and `alice` and clicking through every tab after specs 01–03.

## What works
- Top bar, header, tabs and `/admin` look and behave as specified.
- Home page, demo credentials and project links work.
- "My page" for `alice` lists her reported and assigned issues with all columns.

## To fix
1. **Issues grid has almost no columns.** `/it/issues` and `/it/projects/<id>/issues` only show
   `#`, `Project` and `Actions`. Redmine shows at least Tracker, Status, Priority, Subject,
   Assignee and Updated. Make those the default columns of the issue list (look up in the
   AppJars documentation how Issue Tracker decides the default columns; it is probably a setting
   the demo data has to provide).
2. **Projects grid has no columns at all.** `/it/projects` shows empty rows with only the actions
   menu. Show at least Name, Description and Identifier by default (same approach as above).
3. **"Administration" is shown to non-admin users.** `alice` sees the link in the top bar. Hide it
   for users who are not administrators in Issue Tracker (Issue Tracker has its own admin flag;
   see `specs/STATUS.md`). Keep it simple: one check in `MainLayout`.

## Acceptance criteria
- As `admin`, `/it/issues` shows the five open issues with tracker, status, priority, subject and
  assignee visible.
- As `admin`, `/it/projects` shows both projects with their names.
- As `alice`, the top bar has no "Administration" link.
