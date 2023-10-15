package com.pastebin.controller;

import com.pastebin.dto.UserDTO;
import com.pastebin.entity.User;
import com.pastebin.service.UserDetailsService;
import com.pastebin.service.UserService;
import com.pastebin.util.SecurityUtils;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.NoSuchElementException;

@Controller
@RequestMapping("/user")
public class UserMapping {
    private final UserService userService;
    private final Logger logger = LoggerFactory.getLogger(UserMapping.class);
    private final UserDetailsService userDetailsService;
    private final AuthenticationProvider authenticationProvider;
    private final HttpServletResponse httpServletResponse;
    private final PasswordEncoder passwordEncoder;
    private final SecurityContextHolderStrategy securityContextStrategy = SecurityUtils.securityContextHolderStrategy();
    private final DaoAuthenticationProvider daoAuthenticationProvider;


    @Autowired
    public UserMapping(UserService userService
            , UserDetailsService userDetailsService, AuthenticationProvider authenticationProvider,
                       HttpServletResponse httpServletResponse, PasswordEncoder passwordEncoder,
                       DaoAuthenticationProvider daoAuthenticationProvider) {
        this.userService = userService;
        this.userDetailsService = userDetailsService;
        this.authenticationProvider = authenticationProvider;
        this.httpServletResponse = httpServletResponse;
        this.passwordEncoder = passwordEncoder;
        this.daoAuthenticationProvider = daoAuthenticationProvider;
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String save(@ModelAttribute("user_dto") UserDTO userDTO, Model model) {
        User user = new User(userDTO.getEmail(), userDTO.getPassword());

        logger.info("User {} entered save method", user.getEmail());
        userService.save(user);
        logger.info("User {} was saved!", user.getEmail());

        setAuthenticationInContextHolder(user);
        model.addAttribute("user", user);

        return "welcome";
    }

    @RequestMapping(value = "/perform_login", method = RequestMethod.POST)
    public String login(@ModelAttribute("user_dto") UserDTO userDTO, Model model) {
        User user = userService.findUserByEmail(userDTO.getEmail());

        boolean matches = passwordEncoder.matches(new String(userDTO.getPassword()), user.getPass_bcrypt());
        if (matches) {
            setAuthenticationInContextHolder(user);
            model.addAttribute("user", user);
            return "welcome";
        }

        throw new NoSuchElementException("Wrong credentials! Try again. http://localhost:8080/login");
    }

    public void setAuthenticationInContextHolder(User user) {
        UserDetails userDetails = userDetailsService.loadAuthorizedUserByEntity(user);

        Authentication authReq = new UsernamePasswordAuthenticationToken("test", "test",
                userDetails.getAuthorities());

        logger.info(user.toString());
        logger.info(userDetails.toString());
        logger.info(userDetails.getPassword());
        SecurityContext context = securityContextStrategy.getContext();


        authenticationProvider.authenticate(authReq);
        logger.info("=======================================");
        logger.info(authReq.getCredentials().toString());
        logger.info(context.getAuthentication().toString());

        context.setAuthentication(authReq);
        logger.info("=======================================");
        logger.info(authReq.getCredentials().toString());
        logger.info(context.getAuthentication().toString());

//        daoAuthenticationProvider.authenticate(authReq);
//        logger.info("=======================================");
//        logger.info(authReq.getCredentials().toString());
//        logger.info(context.getAuthentication().toString());
//

    }
}
