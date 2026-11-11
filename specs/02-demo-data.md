# 02 — Demo data on first start

## Goal
When someone runs the app for the first time it must not look empty: there are projects, people
and issues to click around.

## Requirements
1. A Spring component that runs once at startup (`ApplicationRunner`) and only seeds data when no
   project exists yet, so restarting never duplicates anything.
2. Create the data **through the AppJars services** (Issue Tracker and User Manager business
   interfaces), never with SQL or repositories. Find the right services in the documentation.
3. Users (on top of the existing `admin`): `alice` (developer), `bob` (developer),
   `carol` (reporter). Password equal to the username. Make sure they can log in through User
   Manager and that Issue Tracker knows them.
4. Two projects: **Website Redesign** (`website`) and **Mobile App** (`mobile`), with `alice`, `bob`
   and `carol` as members with sensible roles.
5. Issues: at most **five open** issues in total (free-mode limit) spread over both projects and
   over the Bug / Feature / Support trackers, with different priorities and assignees. Add two or
   three **closed** issues as history if the limits allow it; skip them if they do not.
6. Log a few time entries (well below ten per day).
7. If any step fails because of a free-mode limit or a missing reference (e.g. a tracker that does
   not exist), log a warning and continue. Seeding must never stop the application from starting.

## Out of scope
Attachments, custom fields, versions/roadmap, watchers, email notifications.

## Acceptance criteria
- On a fresh start, "Projects" lists both projects and "Issues" shows the seeded issues.
- `alice` / `alice` can log in and sees issues assigned to her on "My page".
- A second start against the same database does not create duplicates (with the default in-memory
  H2 this is trivially true; the check on existing projects must still be there).
