# 03 — Home page

## Goal
Replace the placeholder `HomeView` with a Redmine-like welcome page.

## Requirements
1. Title "Home" and a short welcome paragraph: this is an issue tracker built with AppJars and an AI
   coding agent, as a starting point to experiment with.
2. A "Latest projects" box listing the projects (name linking to the project's issues, plus the
   description), read from the Issue Tracker services.
3. A box with the demo credentials (`admin`/`admin`, `alice`/`alice`, `bob`/`bob`, `carol`/`carol`).
4. Two boxes side by side on wide screens, stacked on narrow ones; boxes with a thin grey border
   (`#D7D7D7`) and a light grey heading, like Redmine's.

## Acceptance criteria
- `/` shows the welcome text, the project list and the credentials box after login.
- Clicking a project goes to a working issues view.
