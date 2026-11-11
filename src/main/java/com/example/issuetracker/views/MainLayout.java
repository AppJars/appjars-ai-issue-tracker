package com.example.issuetracker.views;

import com.appjars.activitylog.flow.view.ActivityLogView;
import com.appjars.issuetracker.business.service.UserService;
import com.appjars.issuetracker.model.UserDto;
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
import com.appjars.usermanager.flow.view.AuthoritiesView;
import com.appjars.usermanager.flow.view.GroupsListView;
import com.appjars.usermanager.flow.view.RulesView;
import com.appjars.usermanager.flow.view.UsersListView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Nav;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.Layout;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.PermitAll;
import java.util.ArrayList;
import java.util.List;

/**
 * The application shell, Redmine style: a thin dark top bar, a blue header with the application
 * name and a row of tabs, and the view content below. AppJar views render inside this layout
 * because each {@code config/*Config} class points the module's {@code RouteConfigurer} at it.
 */
@SuppressWarnings("serial")
// @Layout makes this the automatic parent of every route that does not opt out (LoginView does).
@Layout
// A parent layout needs its own access annotation. Without it, navigating to any view hosted in it
// fails at runtime with a parent-layout access error, even before any security rule is evaluated.
@PermitAll
public class MainLayout extends Div implements RouterLayout, AfterNavigationObserver {

    public static final String APP_NAME = "Issue Tracker";

    /** Views linked from the administration page; "Administration" shows if any is accessible. */
    static final List<Class<?>> ADMIN_VIEWS = List.of(
            ProjectListView.class, TrackerListView.class, IssueStatusListView.class,
            WorkflowSummaryView.class, CustomFieldListView.class, EnumerationListView.class,
            SettingsView.class, UserListView.class, GroupListView.class, RoleListView.class,
            UsersListView.class, AuthoritiesView.class, GroupsListView.class, RulesView.class,
            ActivityLogView.class);

    private final AccessAnnotationChecker accessChecker;
    private final AuthenticationContext authenticationContext;
    private final UserService itUserService;
    private final Div content = new Div();
    private final List<Anchor> tabs = new ArrayList<>();

    public MainLayout(AccessAnnotationChecker accessChecker, AuthenticationContext authenticationContext,
            UserService itUserService) {
        this.accessChecker = accessChecker;
        this.authenticationContext = authenticationContext;
        this.itUserService = itUserService;
        addClassName("rm-shell");
        content.addClassName("rm-content");
        add(createTopBar(), createHeader(), content);
    }

    private Component createTopBar() {
        Nav left = new Nav();
        left.addClassName("rm-top-left");
        left.add(link("Home", HomeView.class));
        if (canAccess(MyPageView.class)) {
            left.add(link("My page", MyPageView.class));
        }
        if (canAccess(ProjectListView.class)) {
            left.add(link("Projects", ProjectListView.class));
        }
        // Issue Tracker guards its admin views with its own admin flag (AdminAccessGuard), which
        // the access annotations checked by canAccess do not reflect.
        boolean issueTrackerAdmin = itUserService.findLoggedInUserOptional().map(UserDto::isAdmin).orElse(false);
        if (issueTrackerAdmin && ADMIN_VIEWS.stream().anyMatch(this::canAccess)) {
            left.add(link("Administration", AdminView.class));
        }

        Div right = new Div();
        right.addClassName("rm-top-right");
        authenticationContext.getPrincipalName().ifPresent(name -> {
            Span loggedIn = new Span("Logged in as ");
            Span user = new Span(name);
            user.addClassName("rm-username");
            loggedIn.add(user);
            right.add(loggedIn);
            if (canAccess(MyAccountView.class)) {
                right.add(link("My account", MyAccountView.class));
            }
            Anchor signOut = new Anchor();
            signOut.setText("Sign out");
            signOut.getElement().addEventListener("click", e -> authenticationContext.logout());
            right.add(signOut);
        });

        Div topBar = new Div(left, right);
        topBar.addClassName("rm-top-bar");
        return topBar;
    }

    private Component createHeader() {
        Span title = new Span(APP_NAME);
        title.addClassName("rm-title");

        Nav tabBar = new Nav();
        tabBar.addClassName("rm-tabs");
        if (canAccess(ProjectListView.class)) {
            tabBar.add(tab("Overview", url(ProjectListView.class)));
        }
        if (canAccess(ProjectActivityView.class)) {
            tabBar.add(tab("Activity", "it/activity"));
        }
        if (canAccess(IssueListComponent.class)) {
            tabBar.add(tab("Issues", "it/issues"));
        }
        if (canAccess(GanttView.class)) {
            tabBar.add(tab("Gantt", "it/issues/gantt"));
        }
        if (canAccess(TimeEntryListView.class)) {
            tabBar.add(tab("Spent time", "it/time_entries"));
        }

        Div header = new Div(title, tabBar);
        header.addClassName("rm-header");
        return header;
    }

    private Anchor tab(String text, String href) {
        Anchor anchor = link(text, href);
        anchor.addClassName("rm-tab");
        tabs.add(anchor);
        return anchor;
    }

    /** A client-side routed link (no full page reload). */
    static Anchor link(String text, String href) {
        Anchor anchor = new Anchor(href, text);
        anchor.getElement().setAttribute("router-link", true);
        return anchor;
    }

    static Anchor link(String text, Class<? extends Component> view) {
        return link(text, url(view));
    }

    static String url(Class<? extends Component> view) {
        return RouteConfiguration.forApplicationScope().getUrl(view);
    }

    /** Whether the current user may open the view. Menu entries of AppJar views are guarded with it. */
    private boolean canAccess(Class<?> view) {
        return canAccess(accessChecker, view);
    }

    static boolean canAccess(AccessAnnotationChecker accessChecker, Class<?> view) {
        return accessChecker.hasAccess(view);
    }

    @Override
    public void showRouterLayoutContent(HasElement view) {
        content.removeAll();
        if (view != null) {
            content.getElement().appendChild(view.getElement());
        }
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        String path = event.getLocation().getPath();
        // The tab whose path is the longest prefix of the current location is the current one,
        // so "it/issues/gantt" selects Gantt rather than Issues.
        Anchor current = tabs.stream()
                .filter(t -> matches(path, t.getHref()))
                .max((a, b) -> Integer.compare(a.getHref().length(), b.getHref().length()))
                .orElse(null);
        tabs.forEach(t -> t.getElement().getClassList().set("selected", t == current));
    }

    private static boolean matches(String path, String href) {
        return path.equals(href) || path.startsWith(href + "/");
    }
}
