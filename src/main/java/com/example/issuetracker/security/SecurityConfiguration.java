package com.example.issuetracker.security;

import static com.vaadin.flow.spring.security.VaadinSecurityConfigurer.vaadin;

import com.appjars.usermanager.flow.security.UserManagerNavigationAccessChecker;
import com.appjars.usermanager.service.util.RememberMeServicesProvider;
import com.example.issuetracker.views.LoginView;
import com.vaadin.flow.spring.security.NavigationAccessControlConfigurer;
import java.util.HashMap;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.ExceptionMappingAuthenticationFailureHandler;

/**
 * Spring Security for a Vaadin 25 application authenticated by User Manager.
 *
 * <p>User Manager provides the {@code PasswordEncoder}, the {@code UserDetailsService} and the
 * {@code RememberMeServicesProvider}; none of them are declared here. Vaadin 25 removed
 * {@code VaadinWebSecurity}: security is configured through a {@link SecurityFilterChain} bean and
 * the {@code vaadin()} configurer.
 */
@EnableWebSecurity
@Configuration
public class SecurityConfiguration {

    /** Route of {@link LoginView}. Naming it in formLogin() keeps Spring Security from generating a login page of its own. */
    public static final String LOGIN_URL = "/login";

    /** Where users land after logging out. */
    public static final String LOGOUT_URL = "/";

    private final RememberMeServicesProvider rememberMeServicesProvider;

    public SecurityConfiguration(RememberMeServicesProvider rememberMeServicesProvider) {
        this.rememberMeServicesProvider = rememberMeServicesProvider;
    }

    /**
     * Required for User Manager's runtime access rules to take effect: registers the checker that
     * evaluates the rules configured in the Rules view against every navigation.
     */
    @Bean
    static NavigationAccessControlConfigurer navigationAccessControlConfigurer() {
        return new NavigationAccessControlConfigurer()
                .withAvailableNavigationAccessCheckers(
                        checker -> checker instanceof UserManagerNavigationAccessChecker);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Public paths must be declared BEFORE vaadin(): it appends a terminal anyRequest matcher
        // and authorizeHttpRequests rules are first-match in declaration order.
        // AntPathRequestMatcher was removed in Spring Security 7; use the requestMatchers overloads.
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers(HttpMethod.GET, "/images/**", "/icons/**", "/*.css", "/*.png")
                .permitAll());

        http.formLogin(formLogin -> formLogin
                .loginPage(LOGIN_URL)
                .permitAll()
                .failureHandler(authenticationFailureHandler()));

        // Persistent "remember me", backed by the tokens User Manager stores in the database.
        http.rememberMe(rememberMe -> rememberMe
                .rememberMeServices(rememberMeServicesProvider.getRememberMeServices())
                .tokenValiditySeconds(7200));

        http.with(vaadin(), configurer -> configurer.loginView(LoginView.class, LOGOUT_URL));

        return http.build();
    }

    /** Maps Spring Security exceptions to the login URLs the login view knows how to explain. */
    private AuthenticationFailureHandler authenticationFailureHandler() {
        Map<String, String> exceptionMappings = new HashMap<>();
        exceptionMappings.put(DisabledException.class.getCanonicalName(), "/login?error=disabled");
        exceptionMappings.put(BadCredentialsException.class.getCanonicalName(), "/login?error=badcredentials");

        ExceptionMappingAuthenticationFailureHandler handler = new ExceptionMappingAuthenticationFailureHandler();
        handler.setExceptionMappings(exceptionMappings);
        handler.setDefaultFailureUrl("/login?error");
        return handler;
    }
}
