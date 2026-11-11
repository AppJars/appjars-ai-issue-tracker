package com.example.issuetracker.views;

import com.appjars.issuetracker.business.service.ProjectService;
import com.appjars.issuetracker.model.ProjectDto;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.html.UnorderedList;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import jakarta.annotation.security.PermitAll;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

/** Redmine-like welcome page: intro text, latest projects and the demo credentials. */
@SuppressWarnings("serial")
@Route("")
// "home" is an alias so menu systems that need a non-empty path (Dynamic Menu) can point at this view.
@RouteAlias("home")
@PageTitle("Home")
@PermitAll
public class HomeView extends Div {

    public HomeView(ProjectService projectService) {
        addClassName("rm-home");
        add(new H2("Home"));
        add(new Paragraph("Welcome! This is an issue tracker built with AppJars (Issue Tracker, User Manager"
                + " and Activity Log) and an AI coding agent. It is a starting point to experiment with:"
                + " browse the projects, open issues, log time and change whatever you like."));

        Div boxes = new Div(latestProjects(projectService), credentials());
        boxes.addClassName("rm-boxes");
        add(boxes);
    }

    private Component latestProjects(ProjectService projectService) {
        List<ProjectDto> projects = projectService.getProjectsAvailableForLoggedInUser().stream()
                .sorted(Comparator.comparing(ProjectDto::getCreatedOn,
                        Comparator.nullsLast(Comparator.<LocalDateTime>reverseOrder())))
                .limit(10)
                .toList();
        Div body = new Div();
        if (projects.isEmpty()) {
            body.add(new Paragraph("No projects yet."));
        } else {
            UnorderedList list = new UnorderedList();
            for (ProjectDto project : projects) {
                ListItem item = new ListItem(MainLayout.link(project.getName(),
                        "it/projects/" + project.getIdentifier() + "/issues"));
                if (project.getDescription() != null && !project.getDescription().isBlank()) {
                    Span description = new Span(project.getDescription());
                    description.addClassName("rm-project-description");
                    item.add(description);
                }
                list.add(item);
            }
            body.add(list);
        }
        return box("Latest projects", body);
    }

    private Component credentials() {
        Div body = new Div(new Paragraph("Demo accounts (password equals the username):"),
                new Html("<table class='rm-credentials'>"
                        + "<tr><td><code>admin</code></td><td><code>admin</code></td><td>administrator</td></tr>"
                        + "<tr><td><code>alice</code></td><td><code>alice</code></td><td>developer</td></tr>"
                        + "<tr><td><code>bob</code></td><td><code>bob</code></td><td>developer</td></tr>"
                        + "<tr><td><code>carol</code></td><td><code>carol</code></td><td>reporter</td></tr>"
                        + "</table>"));
        return box("Demo credentials", body);
    }

    private static Component box(String title, Div body) {
        body.addClassName("rm-box-body");
        Div box = new Div(new H3(title), body);
        box.addClassName("rm-box");
        return box;
    }
}
