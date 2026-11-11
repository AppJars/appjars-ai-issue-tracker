package com.example.issuetracker.demo;

import com.appjars.issuetracker.business.service.EnumerationService;
import com.appjars.issuetracker.business.service.IssueService;
import com.appjars.issuetracker.business.service.IssueStatusService;
import com.appjars.issuetracker.business.service.MemberService;
import com.appjars.issuetracker.business.service.ProjectService;
import com.appjars.issuetracker.business.service.RoleService;
import com.appjars.issuetracker.business.service.TimeEntryService;
import com.appjars.issuetracker.business.service.TrackerService;
import com.appjars.issuetracker.business.service.WorkflowTransitionService;
import com.appjars.issuetracker.model.EnumerationDto;
import com.appjars.issuetracker.model.EnumerationType;
import com.appjars.issuetracker.model.IssueDto;
import com.appjars.issuetracker.model.IssueStatusDto;
import com.appjars.issuetracker.model.ProjectDto;
import com.appjars.issuetracker.model.ProjectStatus;
import com.appjars.issuetracker.model.RoleDto;
import com.appjars.issuetracker.model.TimeEntryDto;
import com.appjars.issuetracker.model.TrackerDto;
import com.appjars.issuetracker.model.WorkflowTransitionDto;
import com.appjars.issuetracker.model.enums.EnabledModule;
import com.appjars.issuetracker.model.enums.IssueVisibility;
import com.appjars.issuetracker.model.enums.RolePermission;
import com.appjars.issuetracker.model.enums.TimeEntryVisibility;
import com.appjars.issuetracker.model.enums.UserVisibility;
import com.appjars.usermanager.model.UserDto;
import java.time.LocalDate;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Seeds demo data on first start (specs/02-demo-data.md), only when Issue Tracker has no project.
 *
 * <p>Everything goes through the AppJars business services. Users are created in User Manager;
 * its {@code UserCreatedEvent} makes Issue Tracker create the matching user. Issue Tracker ships
 * without trackers, statuses, priorities, activities or member roles, so those are created too.
 *
 * <p>Every step is wrapped: a failure (free-mode limit, missing reference) is logged as a warning
 * and seeding continues. Seeding never stops the application from starting.
 */
@Component
public class DemoDataSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DemoDataSeeder.class);

    private final com.appjars.usermanager.service.UserService umUserService;
    private final com.appjars.issuetracker.business.service.UserService itUserService;
    private final PasswordEncoder passwordEncoder;
    private final ProjectService projectService;
    private final MemberService memberService;
    private final RoleService roleService;
    private final TrackerService trackerService;
    private final IssueStatusService statusService;
    private final EnumerationService enumerationService;
    private final WorkflowTransitionService workflowService;
    private final IssueService issueService;
    private final TimeEntryService timeEntryService;

    public DemoDataSeeder(com.appjars.usermanager.service.UserService umUserService,
            com.appjars.issuetracker.business.service.UserService itUserService,
            PasswordEncoder passwordEncoder, ProjectService projectService, MemberService memberService,
            RoleService roleService, TrackerService trackerService, IssueStatusService statusService,
            EnumerationService enumerationService, WorkflowTransitionService workflowService,
            IssueService issueService, TimeEntryService timeEntryService) {
        this.umUserService = umUserService;
        this.itUserService = itUserService;
        this.passwordEncoder = passwordEncoder;
        this.projectService = projectService;
        this.memberService = memberService;
        this.roleService = roleService;
        this.trackerService = trackerService;
        this.statusService = statusService;
        this.enumerationService = enumerationService;
        this.workflowService = workflowService;
        this.issueService = issueService;
        this.timeEntryService = timeEntryService;
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            if (!projectService.findAll().isEmpty()) {
                log.info("Demo data: projects already exist, nothing to seed");
                return;
            }
            // Issue Tracker resolves authorship and permissions from the signed-in user.
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                    "admin", null, List.of(new SimpleGrantedAuthority("ADMIN"))));
            seed();
            log.info("Demo data: seeding finished: {} projects, {} issues ({} open), {} time entries, {} Issue Tracker users",
                    projectService.findAll().size(), issueService.findAll().size(), issueService.countOpenIssues(),
                    timeEntryService.findAll().size(), itUserService.findAll().size());
        } catch (RuntimeException e) {
            log.warn("Demo data: seeding aborted: {}", e.toString());
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    private void seed() {
        // --- Reference data Issue Tracker does not ship with -----------------------------------
        IssueStatusDto statusNew = status("New", false, 1);
        IssueStatusDto statusInProgress = status("In Progress", false, 2);
        IssueStatusDto statusResolved = status("Resolved", false, 3);
        IssueStatusDto statusClosed = status("Closed", true, 4);
        List<IssueStatusDto> statuses = nonNull(statusNew, statusInProgress, statusResolved, statusClosed);

        TrackerDto bug = tracker("Bug", 1, statusNew);
        TrackerDto feature = tracker("Feature", 2, statusNew);
        TrackerDto support = tracker("Support", 3, statusNew);
        Set<TrackerDto> trackers = new HashSet<>(nonNull(bug, feature, support));

        EnumerationDto low = enumeration("Low", EnumerationType.ISSUE_PRIORITY, 1, false);
        EnumerationDto normal = enumeration("Normal", EnumerationType.ISSUE_PRIORITY, 2, true);
        EnumerationDto high = enumeration("High", EnumerationType.ISSUE_PRIORITY, 3, false);
        EnumerationDto urgent = enumeration("Urgent", EnumerationType.ISSUE_PRIORITY, 4, false);
        EnumerationDto development = enumeration("Development", EnumerationType.TIME_ENTRY_ACTIVITY, 1, true);
        EnumerationDto design = enumeration("Design", EnumerationType.TIME_ENTRY_ACTIVITY, 2, false);

        Set<RolePermission> reporterPermissions = EnumSet.of(RolePermission.VIEW_ISSUES,
                RolePermission.ADD_ISSUES, RolePermission.ADD_ISSUE_NOTES, RolePermission.VIEW_GANTT,
                RolePermission.VIEW_TIME_ENTRIES, RolePermission.VIEW_ISSUE_WATCHERS,
                RolePermission.EDIT_OWN_ISSUE_NOTES, RolePermission.SAVE_QUERIES);
        Set<RolePermission> developerPermissions = EnumSet.copyOf(reporterPermissions);
        developerPermissions.addAll(EnumSet.of(RolePermission.EDIT_ISSUES, RolePermission.LOG_TIME,
                RolePermission.EDIT_OWN_TIME_ENTRIES, RolePermission.MANAGE_ISSUE_RELATIONS,
                RolePermission.MANAGE_SUBTASKS, RolePermission.ADD_ISSUE_WATCHERS,
                RolePermission.COPY_ISSUES, RolePermission.VIEW_PRIVATE_NOTES));
        Set<RolePermission> managerPermissions = EnumSet.allOf(RolePermission.class);

        RoleDto manager = role("Manager", 3, managerPermissions);
        RoleDto developer = role("Developer", 4, developerPermissions);
        RoleDto reporter = role("Reporter", 5, reporterPermissions);

        // Every status transition for managers and developers; reporters cannot change status.
        for (TrackerDto tracker : trackers) {
            for (RoleDto role : nonNull(manager, developer)) {
                workflow(tracker, role, statuses);
            }
        }

        // --- Users (User Manager; Issue Tracker mirrors them through UserCreatedEvent) ---------
        var alice = user("alice", "Alice", "Anderson");
        var bob = user("bob", "Bob", "Brown");
        var carol = user("carol", "Carol", "Clark");
        var admin = itUserService.findByUsername("admin").orElse(null);

        // --- Projects and members -------------------------------------------------------------
        ProjectDto website = project("Website Redesign", "website",
                "New look and content for the public company website.", trackers);
        ProjectDto mobile = project("Mobile App", "mobile",
                "iOS and Android companion app for customers.", trackers);

        member(alice, website, manager);
        member(bob, website, developer);
        member(carol, website, reporter);
        member(bob, mobile, manager);
        member(alice, mobile, developer);
        member(carol, mobile, reporter);

        // --- Issues: closed history first (free mode refuses any new issue once five are open),
        // then at most five open issues.
        issue(website, bug, statusClosed, normal, carol, bob,
                "Broken link in footer", "The privacy policy link returns 404.", null);
        issue(mobile, support, statusClosed, low, carol, alice,
                "Reset password email not received", "Turned out to be a spam filter.", null);

        LocalDate today = LocalDate.now();
        IssueDto i1 = issue(website, bug, statusNew, high, carol, alice,
                "Contact form does not send emails",
                "Submitting the contact form shows a success message but no email arrives.", today.plusDays(3));
        IssueDto i2 = issue(website, feature, statusInProgress, normal, alice, bob,
                "New landing page layout",
                "Implement the approved landing page design.", today.plusDays(10));
        IssueDto i3 = issue(mobile, feature, statusNew, normal, bob, alice,
                "Push notifications for order updates",
                "Notify customers when their order changes status.", today.plusDays(14));
        IssueDto i4 = issue(mobile, bug, statusInProgress, urgent, carol, bob,
                "App crashes on login with expired session",
                "Steps: open the app after the session expired, tap Login.", today.plusDays(2));
        issue(mobile, support, statusNew, low, carol, null,
                "Question about supported Android versions",
                "Which Android versions will the first release support?", null);

        // --- Time entries (well below the ten-per-day limit) ---------------------------------
        timeEntry(i1, alice, development, 1.5, "Reproduced the problem", today);
        timeEntry(i2, bob, design, 3.0, "Mock-up review", today.minusDays(1));
        timeEntry(i3, alice, development, 2.0, "Research on push providers", today.minusDays(1));
        timeEntry(i4, bob, development, 2.5, "Investigated crash logs", today);

        if (admin == null) {
            log.warn("Demo data: Issue Tracker has no 'admin' user");
        }
    }

    // --- Steps -----------------------------------------------------------------------------------

    private IssueStatusDto status(String name, boolean closed, int position) {
        return step("status " + name, () -> statusService.findByName(name).orElseGet(() -> {
            IssueStatusDto dto = IssueStatusDto.builder().name(name).isClosed(closed).position(position).build();
            dto.setId(statusService.save(dto));
            return dto;
        }));
    }

    private TrackerDto tracker(String name, int position, IssueStatusDto defaultStatus) {
        if (defaultStatus == null) {
            log.warn("Demo data: skipping tracker {}: no default status", name);
            return null;
        }
        return step("tracker " + name, () -> trackerService.findByName(name).orElseGet(() -> {
            TrackerDto dto = TrackerDto.builder().name(name).position(position).issueStatusDto(defaultStatus)
                    .assigneeField(true).categoryField(true).targetVersionField(true)
                    .parentTaskField(true).startDateField(true).dueDateField(true)
                    .estimatedTimeField(true).percentageDoneField(true).descriptionField(true)
                    .priorityField(true).isInRoadmap(name.equals("Feature")).build();
            dto.setId(trackerService.save(dto, null));
            return dto;
        }));
    }

    private EnumerationDto enumeration(String name, EnumerationType type, int position, boolean isDefault) {
        return step(type + " " + name, () -> enumerationService.findParentByNameAndType(name, type).orElseGet(() -> {
            EnumerationDto dto = EnumerationDto.builder().name(name).type(type).position(position)
                    .defaultValue(isDefault).active(true).build();
            dto.setId(enumerationService.save(dto));
            return dto;
        }));
    }

    private RoleDto role(String name, int position, Set<RolePermission> permissions) {
        return step("role " + name, () -> roleService.findByName(name).orElseGet(() -> {
            RoleDto dto = RoleDto.builder().name(name).position(position).assignable(true).builtin(0)
                    .permissions(permissions).permissionsAllTrackers(permissions)
                    .permissionsTrackersIds(new HashMap<>()).rolesManaged(new HashSet<>())
                    .allRolesManaged(name.equals("Manager"))
                    .issuesVisibility(IssueVisibility.DEFAULT).usersVisibility(UserVisibility.ALL)
                    .timeEntryVisibility(TimeEntryVisibility.ALL).build();
            dto.setId(roleService.save(dto, null));
            return dto;
        }));
    }

    private void workflow(TrackerDto tracker, RoleDto role, List<IssueStatusDto> statuses) {
        step("workflow " + tracker.getName() + "/" + role.getName(), () -> {
            for (IssueStatusDto from : statuses) {
                for (IssueStatusDto to : statuses) {
                    if (from != to) {
                        WorkflowTransitionDto dto = new WorkflowTransitionDto();
                        dto.setTracker(tracker);
                        dto.setRole(role);
                        dto.setOldStatus(from);
                        dto.setNewStatus(to);
                        workflowService.save(dto);
                    }
                }
            }
            return true;
        });
    }

    private com.appjars.issuetracker.model.UserDto user(String username, String firstName, String lastName) {
        step("user " + username, () -> umUserService.findByUsername(username).orElseGet(() -> {
            UserDto dto = new UserDto();
            dto.setUsername(username);
            dto.setEncodedPassword(passwordEncoder.encode(username));
            dto.setEnabled(true);
            dto.setUserAuthorities(new HashSet<>());
            dto.setGroups(new HashSet<>());
            dto.setId(umUserService.save(dto));
            return dto;
        }));
        return step("Issue Tracker user " + username, () -> {
            Optional<com.appjars.issuetracker.model.UserDto> itUser = itUserService.findByUsername(username);
            if (itUser.isEmpty()) {
                log.warn("Demo data: Issue Tracker did not create user {}", username);
                return null;
            }
            com.appjars.issuetracker.model.UserDto dto = itUser.get();
            dto.setFirstName(firstName);
            dto.setLastName(lastName);
            itUserService.update(dto);
            return dto;
        });
    }

    private ProjectDto project(String name, String identifier, String description, Set<TrackerDto> trackers) {
        return step("project " + identifier, () -> {
            ProjectDto dto = new ProjectDto();
            dto.setName(name);
            dto.setIdentifier(identifier);
            dto.setDescription(description);
            dto.setPublicProject(true);
            dto.setStatus(ProjectStatus.ACTIVE);
            dto.setTrackers(trackers);
            dto.setEnabledModules(EnumSet.of(EnabledModule.OVERVIEW, EnabledModule.ACTIVITY,
                    EnabledModule.ISSUES, EnabledModule.TIME_ENTRIES, EnabledModule.GANTT));
            dto.setId(projectService.save(dto, Map.of()));
            return projectService.findById(dto.getId()).orElse(dto);
        });
    }

    private void member(com.appjars.issuetracker.model.UserDto user, ProjectDto project, RoleDto role) {
        if (user == null || project == null || role == null) {
            return;
        }
        step("member " + user.getLogin() + "@" + project.getIdentifier(),
                () -> memberService.save(user, project, Set.of(role)));
    }

    private IssueDto issue(ProjectDto project, TrackerDto tracker, IssueStatusDto status, EnumerationDto priority,
            com.appjars.issuetracker.model.UserDto author, com.appjars.issuetracker.model.UserDto assignee,
            String subject, String description, LocalDate dueDate) {
        if (project == null || tracker == null || status == null || priority == null || author == null) {
            log.warn("Demo data: skipping issue '{}': missing reference", subject);
            return null;
        }
        return step("issue '" + subject + "'", () -> {
            IssueDto dto = new IssueDto();
            dto.setProject(project);
            dto.setTracker(tracker);
            dto.setStatus(status);
            dto.setPriority(priority);
            dto.setAuthor(author);
            dto.setAssignedUser(assignee);
            dto.setSubject(subject);
            dto.setDescription(description);
            dto.setStartDate(LocalDate.now().minusDays(2));
            dto.setDueDate(dueDate);
            dto.setDoneRatio(status.isClosed() ? 100 : 0);
            dto.setId(issueService.save(dto, List.of(), Map.of()));
            return dto;
        });
    }

    private void timeEntry(IssueDto issue, com.appjars.issuetracker.model.UserDto user, EnumerationDto activity,
            double hours, String comments, LocalDate spentOn) {
        if (issue == null || user == null || activity == null) {
            return;
        }
        step("time entry '" + comments + "'", () -> {
            TimeEntryDto dto = new TimeEntryDto();
            dto.setProject(issue.getProject());
            dto.setIssue(issue);
            dto.setUser(user);
            dto.setAuthor(user);
            dto.setActivity(activity);
            dto.setHours(hours);
            dto.setComments(comments);
            dto.setSpentOn(spentOn);
            return timeEntryService.save(dto, Map.of());
        });
    }

    // --- Helpers -----------------------------------------------------------------------------------

    /** Runs one seeding step; on failure logs a warning and returns null so seeding continues. */
    private <T> T step(String what, Supplier<T> action) {
        try {
            return action.get();
        } catch (RuntimeException e) {
            log.warn("Demo data: could not create {}: {}", what, e.toString());
            return null;
        }
    }

    @SafeVarargs
    private static <T> List<T> nonNull(T... items) {
        return java.util.Arrays.stream(items).filter(java.util.Objects::nonNull).toList();
    }
}
