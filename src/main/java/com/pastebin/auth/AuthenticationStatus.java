package com.pastebin.auth;

import com.pastebin.dto.UserDTO;
import com.pastebin.entity.User;
import com.pastebin.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * The AuthenticationStatus class is responsible for managing the authentication status of a user.
 * It provides methods to check if a user is authenticated, retrieve the authenticated user,
 * and authenticate a user with the provided credentials.
 */
@Component
public class AuthenticationStatus {
    private final AuthenticationManager authenticationManager;
    private final Logger logger = LoggerFactory.getLogger(AuthenticationStatus.class);
    private final UserService userService;
    private final HttpSession session;

    @Autowired
    public AuthenticationStatus(AuthenticationManager authenticationManager, UserService userService, HttpSession session) {
        this.userService = userService;
        this.session = session;
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_GLOBAL);
        this.authenticationManager = authenticationManager;
    }

    public boolean isUserAuthenticated() {
        try {
            return getSecurityContext().getAuthentication().isAuthenticated();
        } catch (NullPointerException e) {
            return false;
        }
    }

    public User getAuthenticatedUser() {
        return userService.findUserByEmail(getSecurityContext().getAuthentication()
                .getPrincipal().toString());
    }

    private SecurityContext getSecurityContext() {
        return (SecurityContext) session.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
    }

    /**
     * Authenticates a user based on the provided userDTO.
     *
     * @param userDTO the user data transfer object containing the email and password
     * @param <T>     the type of userDTO
     */
    public <T extends UserDTO> void authenticateUser(T userDTO) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(userDTO.getEmail(), new String(userDTO.getPassword()), Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));

        authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        if (usernamePasswordAuthenticationToken.isAuthenticated()) {
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
            logger.info(String.format("Auto login %s successfully!", userDTO.getEmail()));
        }
    }
}
