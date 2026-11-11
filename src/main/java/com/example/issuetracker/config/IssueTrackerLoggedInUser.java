package com.example.issuetracker.config;

import com.appjars.issuetracker.business.service.LoggedInUsernameProvider;
import com.vaadin.flow.spring.security.AuthenticationContext;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * Tells Issue Tracker who is signed in. Issue Tracker requires this bean (the application does not
 * start without it) and resolves the matching Issue Tracker user from the login name.
 */
@Component
public class IssueTrackerLoggedInUser implements LoggedInUsernameProvider {

    private final AuthenticationContext authenticationContext;

    public IssueTrackerLoggedInUser(AuthenticationContext authenticationContext) {
        this.authenticationContext = authenticationContext;
    }

    @Override
    public Optional<String> getLoggedInUsername() {
        return authenticationContext.getPrincipalName();
    }
}
