Small change to the demo data (spec 02): the seeder creates 5 open issues, which is exactly the
free-mode limit, so a new user cannot create an issue without closing one first.

1. Change `DemoDataSeeder` so it creates **3 open issues** (keep a mix of trackers, priorities and
   assignees across both projects) and turn the other two into closed issues, so the history
   still looks lived-in. That leaves room for two new issues in free mode.
2. Update spec 02 (`specs/02-demo-data.md`) to say "at most three open issues" and why.
3. Run `mvn -q -DskipTests package`, start the app with `mvn spring-boot:run`, check the seeding
   log line and that there are no errors, then stop it.
4. Update `specs/STATUS.md`.
