package com.pastebin.auth;

import com.pastebin.dto.UserDTO;
import com.pastebin.entity.User;
import com.pastebin.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * The AuthenticationContext class is responsible for managing the authentication status of a user.
 * It provides methods to check if a user is authenticated, retrieve the authenticated user,
 * and authenticate a user with the provided credentials.
 */
@Component
public class AuthenticationContext {
    private final AuthenticationManager authenticationManager;
    private final Logger logger = LoggerFactory.getLogger(AuthenticationContext.class);
    private final UserService userService;
    private final HttpSession session;
    private final GrantedAuthority DEFAULT_AUTHORITY = new SimpleGrantedAuthority("ROLE_USER");
    private User authenticatedUser = null;

    @Autowired
    public AuthenticationContext(AuthenticationManager authenticationManager, UserService userService, HttpSession session) {
        this.userService = userService;
        this.session = session;
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_GLOBAL);
        this.authenticationManager = authenticationManager;
    }

    private Authentication getAuthentication() {
        SecurityContext context = getSecurityContext();
        return context != null ? context.getAuthentication() : null;
    }

    public boolean isUserAuthenticated() {
        try {
            return getSecurityContext().getAuthentication().isAuthenticated(); //context also may be null on app start
        } catch (NullPointerException e) {
            return false;
        }
    }

    /**
     * Authenticates a user based on the provided userDTO.
     *
     * @param userDTO the user data transfer object containing the email and password
     * @param <T>     the type of userDTO
     */
    public <T extends UserDTO> void setUserAuthenticated(T userDTO) {

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(userDTO.getEmail(),
                        new String(userDTO.getPassword()),
                        Collections.singletonList(DEFAULT_AUTHORITY));

        authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        if (usernamePasswordAuthenticationToken.isAuthenticated()) {
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());

        } else {
            throw new AuthenticationServiceException("Something wrong during your authentication process. Please try again!");
        }
    }

    public User getAuthenticatedUser() {
        if (this.authenticatedUser == null && getAuthentication() != null) {
            this.authenticatedUser = userService.findUserByEmail(getAuthentication().getPrincipal().toString()).get();
        }
        return this.authenticatedUser;
    }

    private SecurityContext getSecurityContext() {
        return (SecurityContext) session.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
    }

    public void removeAuthentication() {
        getSecurityContext().getAuthentication().setAuthenticated(false);
        authenticatedUser = null;
    }
}