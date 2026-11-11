package com.example.issuetracker.views;

import com.appjars.usermanager.flow.view.UserManagerLoginView;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

/**
 * The login page, provided by the module that authenticates users. It renders full-screen, so it
 * opts out of the automatic {@link MainLayout} parent.
 */
@SuppressWarnings("serial")
@Route(value = "login", autoLayout = false)
@AnonymousAllowed
public class LoginView extends UserManagerLoginView {
}
