package com.example.issuetracker.views;

import com.appjars.activitylog.flow.view.ActivityLogView;
import com.appjars.activitylog.flow.view.ExtractorsListView;
import com.appjars.activitylog.flow.view.LogViewerListView;
import com.appjars.activitylog.flow.view.RemoversListView;
import com.appjars.issuetracker.flow.component.IssueListComponent;
import com.appjars.issuetracker.flow.view.CustomFieldListView;
import com.appjars.issuetracker.flow.view.EnumerationListView;
import com.appjars.issuetracker.flow.view.GanttView;
import com.appjars.issuetracker.flow.view.GroupListView;
import com.appjars.issuetracker.flow.view.IssueStatusListView;
import com.appjars.issuetracker.flow.view.MyAccountView;
import com.appjars.issuetracker.flow.view.MyPageView;
import com.appjars.issuetracker.flow.view.ProjectActivityView;
import com.appjars.issuetracker.flow.view.ProjectListView;
import com.appjars.issuetracker.flow.view.RoleListView;
import com.appjars.issuetracker.flow.view.SettingsView;
import com.appjars.issuetracker.flow.view.TimeEntryListView;
import com.appjars.issuetracker.flow.view.TrackerListView;
import com.appjars.issuetracker.flow.view.UserListView;
import com.appjars.issuetracker.flow.view.WorkflowSummaryView;
import com.appjars.usermanager.flow.view.AuthProvidersListView;
import com.appjars.usermanager.flow.view.AuthoritiesView;
import com.appjars.usermanager.flow.view.GroupsListView;
import com.appjars.usermanager.flow.view.ProfileView;
import com.appjars.usermanager.flow.view.RulesView;
import com.appjars.usermanager.flow.view.UsersListView;
import com.appjars.usermanager.flow.view.ViewsView;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Layout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.PermitAll;

/**
 * The application shell: a drawer with the navigation and a header showing the current view title.
 * AppJar views render inside this layout because each {@code config/*Config} class points the
 * module's {@code RouteConfigurer} at it.
 */
@SuppressWarnings("serial")
// @Layout makes this the automatic parent of every route that does not opt out (LoginView does).
@Layout
// A parent layout needs its own access annotation. Without it, navigating to any view hosted in it
// fails at runtime with a parent-layout access error, even before any security rule is evaluated.
@PermitAll
public class MainLayout extends AppLayout implements AfterNavigationObserver {

    private static final String APP_NAME = "Issue Tracker";

    private final AccessAnnotationChecker accessChecker;
    private final AuthenticationContext authenticationContext;
    private final H2 viewTitle = new H2();

    public MainLayout(AccessAnnotationChecker accessChecker, AuthenticationContext authenticationContext) {
        this.accessChecker = accessChecker;
        this.authenticationContext = authenticationContext;
        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");
        viewTitle.addClassName("view-title");
        addToNavbar(true, toggle, viewTitle);
    }

    private void addDrawerContent() {
        Span appName = new Span(APP_NAME);
        appName.addClassName("app-name");
        addToDrawer(new Header(appName), new Scroller(createNavigation()), createFooter());
    }

    private SideNav createNavigation() {
        SideNav nav = new SideNav();
        nav.addItem(new SideNavItem("Home", HomeView.class, VaadinIcon.HOME.create()));

        // --- User Manager ---------------------------------------------------------------------
        // Routes are registered by User Manager's RouteConfigurer (see config/); only the menu entries
        // are declared here. Every view the module provides is listed, so nothing is hidden by
        // default: remove what your application does not need.
        if (canAccess(UsersListView.class) || canAccess(AuthoritiesView.class) || canAccess(GroupsListView.class) || canAccess(RulesView.class) || canAccess(ViewsView.class) || canAccess(AuthProvidersListView.class) || canAccess(ProfileView.class)) {
            SideNavItem userManagerSection = new SideNavItem("Security");
            userManagerSection.setPrefixComponent(VaadinIcon.SHIELD.create());
            if (canAccess(UsersListView.class)) {
                userManagerSection.addItem(new SideNavItem("Users", UsersListView.class, VaadinIcon.USER.create()));
            }
            if (canAccess(AuthoritiesView.class)) {
                userManagerSection.addItem(new SideNavItem("Roles", AuthoritiesView.class, VaadinIcon.KEY.create()));
            }
            if (canAccess(GroupsListView.class)) {
                userManagerSection.addItem(new SideNavItem("Groups", GroupsListView.class, VaadinIcon.USERS.create()));
            }
            if (canAccess(RulesView.class)) {
                userManagerSection.addItem(new SideNavItem("Rules", RulesView.class, VaadinIcon.LOCK.create()));
            }
            if (canAccess(ViewsView.class)) {
                userManagerSection.addItem(new SideNavItem("Views", ViewsView.class, VaadinIcon.EYE.create()));
            }
            if (canAccess(AuthProvidersListView.class)) {
                userManagerSection.addItem(new SideNavItem("Authentication Providers", AuthProvidersListView.class, VaadinIcon.SIGN_IN.create()));
            }
            if (canAccess(ProfileView.class)) {
                userManagerSection.addItem(new SideNavItem("My Profile", ProfileView.class, VaadinIcon.USER_CARD.create()));
            }
            nav.addItem(userManagerSection);
        }

        // --- Activity Log ---------------------------------------------------------------------
        // Routes are registered by Activity Log's RouteConfigurer (see config/); only the menu entries
        // are declared here. Every view the module provides is listed, so nothing is hidden by
        // default: remove what your application does not need.
        if (canAccess(ActivityLogView.class) || canAccess(ExtractorsListView.class) || canAccess(RemoversListView.class) || canAccess(LogViewerListView.class)) {
            SideNavItem activityLogSection = new SideNavItem("Activity Log");
            activityLogSection.setPrefixComponent(VaadinIcon.FILE_SEARCH.create());
            if (canAccess(ActivityLogView.class)) {
                activityLogSection.addItem(new SideNavItem("Logs", ActivityLogView.class, VaadinIcon.RECORDS.create()));
            }
            if (canAccess(ExtractorsListView.class)) {
                activityLogSection.addItem(new SideNavItem("Extractors", ExtractorsListView.class, VaadinIcon.FILTER.create()));
            }
            if (canAccess(RemoversListView.class)) {
                activityLogSection.addItem(new SideNavItem("Removers", RemoversListView.class, VaadinIcon.TRASH.create()));
            }
            if (canAccess(LogViewerListView.class)) {
                activityLogSection.addItem(new SideNavItem("Log Views", LogViewerListView.class, VaadinIcon.EYE.create()));
            }
            nav.addItem(activityLogSection);
        }

        // --- Issue Tracker ---------------------------------------------------------------------
        // Routes are registered by Issue Tracker's RouteConfigurer (see config/); only the menu entries
        // are declared here. Every view the module provides is listed, so nothing is hidden by
        // default: remove what your application does not need.
        if (canAccess(MyPageView.class) || canAccess(ProjectListView.class) || canAccess(ProjectActivityView.class) || canAccess(IssueListComponent.class) || canAccess(GanttView.class) || canAccess(TimeEntryListView.class) || canAccess(MyAccountView.class) || canAccess(UserListView.class) || canAccess(GroupListView.class) || canAccess(RoleListView.class) || canAccess(TrackerListView.class) || canAccess(IssueStatusListView.class) || canAccess(WorkflowSummaryView.class) || canAccess(CustomFieldListView.class) || canAccess(EnumerationListView.class) || canAccess(SettingsView.class)) {
            SideNavItem issueTrackerSection = new SideNavItem("Issue Tracker");
            issueTrackerSection.setPrefixComponent(VaadinIcon.BUG.create());
            if (canAccess(MyPageView.class)) {
                issueTrackerSection.addItem(new SideNavItem("My Page", MyPageView.class, VaadinIcon.USER.create()));
            }
            if (canAccess(ProjectListView.class)) {
                issueTrackerSection.addItem(new SideNavItem("Projects", ProjectListView.class, VaadinIcon.FOLDER.create()));
            }
            if (canAccess(ProjectActivityView.class)) {
                issueTrackerSection.addItem(new SideNavItem("Activity", "it/activity", VaadinIcon.CLOCK.create()));
            }
            if (canAccess(IssueListComponent.class)) {
                issueTrackerSection.addItem(new SideNavItem("Issues", "it/issues", VaadinIcon.BUG.create()));
            }
            if (canAccess(GanttView.class)) {
                issueTrackerSection.addItem(new SideNavItem("Gantt", "it/issues/gantt", VaadinIcon.CHART_TIMELINE.create()));
            }
            if (canAccess(TimeEntryListView.class)) {
                issueTrackerSection.addItem(new SideNavItem("Spent Time", "it/time_entries", VaadinIcon.TIMER.create()));
            }
            if (canAccess(MyAccountView.class)) {
                issueTrackerSection.addItem(new SideNavItem("My Account", MyAccountView.class, VaadinIcon.USER_CARD.create()));
            }
            if (canAccess(UserListView.class)) {
                issueTrackerSection.addItem(new SideNavItem("Users", UserListView.class, VaadinIcon.USERS.create()));
            }
            if (canAccess(GroupListView.class)) {
                issueTrackerSection.addItem(new SideNavItem("Groups", GroupListView.class, VaadinIcon.GROUP.create()));
            }
            if (canAccess(RoleListView.class)) {
                issueTrackerSection.addItem(new SideNavItem("Roles", RoleListView.class, VaadinIcon.KEY.create()));
            }
            if (canAccess(TrackerListView.class)) {
                issueTrackerSection.addItem(new SideNavItem("Trackers", TrackerListView.class, VaadinIcon.TAGS.create()));
            }
            if (canAccess(IssueStatusListView.class)) {
                issueTrackerSection.addItem(new SideNavItem("Issue Statuses", IssueStatusListView.class, VaadinIcon.FLAG_CHECKERED.create()));
            }
            if (canAccess(WorkflowSummaryView.class)) {
                issueTrackerSection.addItem(new SideNavItem("Workflows", WorkflowSummaryView.class, VaadinIcon.SITEMAP.create()));
            }
            if (canAccess(CustomFieldListView.class)) {
                issueTrackerSection.addItem(new SideNavItem("Custom Fields", CustomFieldListView.class, VaadinIcon.INPUT.create()));
            }
            if (canAccess(EnumerationListView.class)) {
                issueTrackerSection.addItem(new SideNavItem("Enumerations", EnumerationListView.class, VaadinIcon.LIST_OL.create()));
            }
            if (canAccess(SettingsView.class)) {
                issueTrackerSection.addItem(new SideNavItem("Settings", SettingsView.class, VaadinIcon.COG.create()));
            }
            nav.addItem(issueTrackerSection);
        }
        return nav;
    }

    /** Whether the current user may open the view. Menu entries of AppJar views are guarded with it. */
    private boolean canAccess(Class<?> view) {
        return accessChecker.hasAccess(view);
    }

    /** For views that carry no access annotation and leave access control to the application. */
    private boolean isSignedIn() {
        return authenticationContext.isAuthenticated();
    }

    private Footer createFooter() {
        Footer footer = new Footer();
        footer.addClassName("drawer-footer");
        authenticationContext.getPrincipalName().ifPresentOrElse(name -> {
            Button signOut = new Button("Sign out", event -> authenticationContext.logout());
            signOut.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
            footer.add(new Avatar(name), new Span(name), signOut);
        }, () -> footer.add(new Span("Not signed in")));
        return footer;
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        viewTitle.setText(currentPageTitle());
    }

    private String currentPageTitle() {
        if (getContent() instanceof HasDynamicTitle dynamic) {
            return dynamic.getPageTitle();
        }
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? APP_NAME : title.value();
    }
}
