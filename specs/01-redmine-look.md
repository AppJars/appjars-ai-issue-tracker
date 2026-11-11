# 01 — A Redmine-style shell and theme

## Goal
Anyone who has used Redmine should recognise the layout at a glance: a thin dark top bar, a blue
header with the application name, a row of tabs under it, and dense white content below. It only has
to *evoke* Redmine; it does not have to copy it pixel by pixel.

## Requirements
1. Replace the drawer navigation in `MainLayout` with a top layout. No drawer, no drawer toggle.
2. **Top bar** (height ~24px, background `#3E5B76`, small white links):
   - Left: `Home`, `My page`, `Projects`, `Administration` (the last one only for users who can
     access the administration views).
   - Right: `Logged in as <username>`, `My account`, `Sign out`.
3. **Header** (background `#628DB6`, white text): the application name "Issue Tracker" as a large
   title (~24px) on the left.
4. **Tabs** directly under the header, aligned left: `Overview`, `Activity`, `Issues`, `Gantt`,
   `Spent time`. Tabs have a slightly lighter blue background (`#759FCF`) with white text; the tab of
   the current view is white with dark text. Use plain anchors or `RouterLink`s, not Vaadin `Tabs`.
5. **Content area**: white background, 10–12px padding, base font size 13px, sans-serif
   (`Verdana, sans-serif` is fine). Links `#116699`, underlined on hover.
6. **Grids**: compact row height, alternate row background `#F6F7F8`, header row with light grey
   background (`#EEEEEE`) and bold text. Apply it globally through Lumo CSS custom properties and
   `vaadin-grid` part selectors in `styles.css`.
7. **Buttons**: small radius (3px), primary colour `#3E5B76`.
8. **Administration page** at `/admin`: a simple list of links, Redmine-style, grouped as
   - *Issue tracking*: Projects, Trackers, Issue statuses, Workflows, Custom fields, Enumerations, Settings
   - *Users and permissions*: Users, Groups, Roles (Issue Tracker) and Users, Roles, Groups, Access rules (User Manager, under the heading "Security")
   - *Audit*: Activity Log
   Every link only appears when the current user can access its view (reuse the `canAccess` helper
   already in `MainLayout`).

## Out of scope
Project-specific menus (tabs that change per project), the right-hand sidebar, icons in the
menu, mobile layout.

## Acceptance criteria
- After login, the page shows the dark top bar, the blue header and the tabs; there is no drawer.
- Every link in the top bar and tabs navigates to a working view.
- `/admin` lists the administration links for the `admin` user.
- Grids in "Issues" look compact with striped rows.
