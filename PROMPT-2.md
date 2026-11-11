I tested the application by hand. The results are in `specs/04-test-findings.md`.

1. Read `specs/04-test-findings.md` and `specs/STATUS.md`.
2. Use the `appjars-docs` MCP server to find out how Issue Tracker decides the default columns of
   its issue and project lists, and how to tell whether the current user is an Issue Tracker
   administrator.
3. Fix the three findings. Keep the changes small.
4. Run `mvn -q -DskipTests package`, start the app with `mvn spring-boot:run`, check the log for
   errors, then stop it.
5. Add a row for spec 04 to `specs/STATUS.md` and update "Not done".
