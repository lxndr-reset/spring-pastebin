package com.pastebin.auth;

import com.pastebin.dto.UserDTO;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class Authentication {
    private final AuthenticationManager authenticationManager;
    private final Logger logger = LoggerFactory.getLogger(Authentication.class);
    private final HttpSession session;

    @Autowired
    public Authentication(AuthenticationManager authenticationManager, HttpSession session) {
        this.session = session;
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_GLOBAL);
        this.authenticationManager = authenticationManager;
    }

    public boolean isUserAuthenticated() {
        return SecurityContextHolder.getContext().getAuthentication().isAuthenticated();
    }

    public <T extends UserDTO> void authenticateUser(T userDTO) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(userDTO.getEmail(), new String(userDTO.getPassword()), Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));

        authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        if (usernamePasswordAuthenticationToken.isAuthenticated()) {
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
            logger.debug(String.format("Auto login %s successfully!", userDTO.getEmail()));
        }
    }
}
