package com.example.issuetracker.config;

import com.appjars.usermanager.flow.util.RouteConfigurer;
import com.example.issuetracker.views.MainLayout;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;

/**
 * Wires the User Manager AppJar into this application.
 *
 * <p>The module registers its views at startup through its own {@code RouteConfigurer}. Pointing
 * it at {@link MainLayout} makes those views render inside this application's navigation shell.
 * The {@code @Qualifier} is required: every AppJar contributes a bean of a class named
 * {@code RouteConfigurer}, and their simple names collide.
 *
 * <p>Documentation: https://docs.appjars.com/user-manager/overview/
 */
@Configuration
public class UserManagerConfig {

    private final RouteConfigurer routeConfigurer;

    public UserManagerConfig(@Qualifier("UserManagerRouteConfigurer") RouteConfigurer routeConfigurer) {
        this.routeConfigurer = routeConfigurer;
    }

    @PostConstruct
    void configureRoutes() {
        routeConfigurer.setViewsRouterLayout(MainLayout.class);
    }
}
