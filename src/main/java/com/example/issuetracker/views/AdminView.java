package com.example.issuetracker.views;

import com.appjars.activitylog.flow.view.ActivityLogView;
import com.appjars.issuetracker.flow.view.CustomFieldListView;
import com.appjars.issuetracker.flow.view.EnumerationListView;
import com.appjars.issuetracker.flow.view.GroupListView;
import com.appjars.issuetracker.flow.view.IssueStatusListView;
import com.appjars.issuetracker.flow.view.ProjectListView;
import com.appjars.issuetracker.flow.view.RoleListView;
import com.appjars.issuetracker.flow.view.SettingsView;
import com.appjars.issuetracker.flow.view.TrackerListView;
import com.appjars.issuetracker.flow.view.UserListView;
import com.appjars.issuetracker.flow.view.WorkflowSummaryView;
import com.appjars.usermanager.flow.view.AuthoritiesView;
import com.appjars.usermanager.flow.view.GroupsListView;
import com.appjars.usermanager.flow.view.RulesView;
import com.appjars.usermanager.flow.view.UsersListView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.UnorderedList;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import jakarta.annotation.security.PermitAll;
import java.util.LinkedHashMap;
import java.util.Map;

/** Redmine-style administration page: grouped links to the AppJars' administration views. */
@SuppressWarnings("serial")
@Route("admin")
@PageTitle("Administration")
@PermitAll
public class AdminView extends Div {

    private final AccessAnnotationChecker accessChecker;

    public AdminView(AccessAnnotationChecker accessChecker) {
        this.accessChecker = accessChecker;
        addClassName("rm-admin");
        add(new H2("Administration"));

        Map<String, Class<? extends Component>> issueTracking = new LinkedHashMap<>();
        issueTracking.put("Projects", ProjectListView.class);
        issueTracking.put("Trackers", TrackerListView.class);
        issueTracking.put("Issue statuses", IssueStatusListView.class);
        issueTracking.put("Workflows", WorkflowSummaryView.class);
        issueTracking.put("Custom fields", CustomFieldListView.class);
        issueTracking.put("Enumerations", EnumerationListView.class);
        issueTracking.put("Settings", SettingsView.class);
        section("Issue tracking", issueTracking);

        Map<String, Class<? extends Component>> users = new LinkedHashMap<>();
        users.put("Users", UserListView.class);
        users.put("Groups", GroupListView.class);
        users.put("Roles", RoleListView.class);
        section("Users and permissions", users);

        Map<String, Class<? extends Component>> security = new LinkedHashMap<>();
        security.put("Users", UsersListView.class);
        security.put("Roles", AuthoritiesView.class);
        security.put("Groups", GroupsListView.class);
        security.put("Access rules", RulesView.class);
        section("Security", security);

        Map<String, Class<? extends Component>> audit = new LinkedHashMap<>();
        audit.put("Activity Log", ActivityLogView.class);
        section("Audit", audit);

        if (getChildren().count() == 1) {
            add(new Paragraph("You do not have access to any administration view."));
        }
    }

    private void section(String title, Map<String, Class<? extends Component>> links) {
        UnorderedList list = new UnorderedList();
        links.forEach((text, view) -> {
            if (MainLayout.canAccess(accessChecker, view)) {
                list.add(new ListItem(MainLayout.link(text, view)));
            }
        });
        if (list.getChildren().findAny().isPresent()) {
            add(new H3(title), list);
        }
    }
}
