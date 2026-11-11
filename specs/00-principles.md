# 00 — Principles

These rules apply to every spec in this folder. When a spec and a principle disagree, the principle wins.

1. **Reuse before you write.** Issue Tracker, User Manager and Activity Log already provide projects,
   issues, workflows, time tracking, users, roles and audit logs. Use their views and services. Never
   re-implement something an AppJar already does. Look it up in the AppJars documentation (the
   `appjars-docs` MCP server) before writing code.
2. **Small and running beats complete.** This is a starter to play with, not a product. If a feature
   gets complicated, drop it and note it under "Not done" in `specs/STATUS.md`.
3. **Follow `AGENTS.md`.** Its "Rules that are easy to break" are hard constraints (Vaadin 25: plain
   CSS, no `@Theme`; keep `@PermitAll` on the layout; keep `vaadin.allowed-packages`; Maven only).
4. **Plain CSS for the look.** Styling goes in `src/main/resources/META-INF/resources/styles.css`.
   No new frontend dependencies, no npm packages, no web fonts.
5. **Respect free mode.** No license key is configured. Issue Tracker's free mode allows at most
   five users and five open issues. Demo data must stay inside those limits.
6. **Done means verified.** `mvn -q -DskipTests package` passes and the application boots with
   `mvn spring-boot:run` without errors in the log.
