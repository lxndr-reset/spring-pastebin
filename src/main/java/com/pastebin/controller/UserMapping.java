package com.pastebin.controller;

import com.pastebin.entity.User;
import com.pastebin.service.UserDetailsService;
import com.pastebin.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/user")
public class UserMapping {
    private final UserService userService;
    private final Logger logger = LoggerFactory.getLogger(UserMapping.class);
    private final UserDetailsService userDetailsService;
    private final AuthenticationProvider authenticationProvider;
    private final HttpServletResponse httpServletResponse;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserMapping(UserService userService
            , UserDetailsService userDetailsService, AuthenticationProvider authenticationProvider,
                       HttpServletResponse httpServletResponse, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.userDetailsService = userDetailsService;
        this.authenticationProvider = authenticationProvider;
        this.httpServletResponse = httpServletResponse;
        this.passwordEncoder = passwordEncoder;
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String save(@ModelAttribute("user") User user, Model model,
                       @ModelAttribute("password") String password) {


        user.setPass_bcrypt(password);
        logger.info("User {} entered save method", user.getEmail());
        userService.save(user);
        logger.info("User {} was saved!", user.getEmail());

        setAuthenticationInContextHolder(user);

        return "welcome";
    }

    @RequestMapping(value = "/perform_login", method = RequestMethod.POST)
    public void login(Model model, @ModelAttribute("user") User user, @ModelAttribute("password") String password) {
        User compareUser = userService.findUserByEmail(user.getEmail());

        boolean matches = passwordEncoder.matches(password, compareUser.getPass_bcrypt());
        if (matches) {
            setAuthenticationInContextHolder(user);
        }
        return;
    }

    public void setAuthenticationInContextHolder(User user) {
        UserDetails userDetails = userDetailsService.loadUserByEntity(user);
        UsernamePasswordAuthenticationToken authReq =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//        authenticationProvider.authenticate(authReq);
        SecurityContextHolder.getContext().setAuthentication(authReq);
        return;
    }

}
