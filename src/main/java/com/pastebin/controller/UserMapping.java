package com.pastebin.controller;

import com.pastebin.dto.UserDTO;
import com.pastebin.entity.User;
import com.pastebin.service.UserDetailsService;
import com.pastebin.service.UserService;
import com.pastebin.util.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Collections;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("/user")
public class UserMapping {
    private final UserService userService;
    private final Logger logger = LoggerFactory.getLogger(UserMapping.class);
    private final UserDetailsService userDetailsService;
    private final AuthenticationProvider authenticationProvider;
    private final PasswordEncoder passwordEncoder;
    private final SecurityContextHolderStrategy securityContextStrategy = SecurityUtils.securityContextHolderStrategy();
    private final AuthenticationManager authenticationManager;

    @Autowired
    public UserMapping(UserService userService, UserDetailsService userDetailsService,
                       AuthenticationProvider authenticationProvider, PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.userDetailsService = userDetailsService;
        this.authenticationProvider = authenticationProvider;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String save(@ModelAttribute("user_dto") UserDTO userDTO, Model model) {
        User user = new User(userDTO.getEmail(), userDTO.getPassword());

        userService.save(user);

        setAuthenticationInContextHolder(userDTO);
        userDTO = null;
        model.addAttribute("user", user);

        return "welcome";
    }

    @RequestMapping(value = "/perform_login", method = RequestMethod.POST)
    public String login(@ModelAttribute("user_dto") UserDTO userDTO, Model model) {
        User user = userService.findUserByEmail(userDTO.getEmail());

        boolean matches = passwordEncoder.matches(new String(userDTO.getPassword()), user.getPass_bcrypt());
        if (matches) {
            setAuthenticationInContextHolder(userDTO);
            userDTO = null;
            model.addAttribute("user", user);

            return "welcome";
        }

        throw new NoSuchElementException("Wrong credentials! Try again. http://localhost:8080/login");
    }

    public void setAuthenticationInContextHolder(UserDTO userDTO) {
        Authentication authReq = new UsernamePasswordAuthenticationToken(userDTO.getEmail(), new String(userDTO.getPassword()),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        authenticationManager.authenticate(authReq);
        SecurityContext context = securityContextStrategy.getContext();
        authenticationProvider.authenticate(authReq);
        context.setAuthentication(authReq);

    }
}
