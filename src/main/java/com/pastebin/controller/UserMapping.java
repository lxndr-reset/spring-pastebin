package com.pastebin.controller;

import com.pastebin.entity.User;
import com.pastebin.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final HttpSecurity httpSecurity;


    @Autowired
    public UserMapping(UserService userService, HttpSecurity httpSecurity) throws Exception {
        this.userService = userService;
        this.httpSecurity = httpSecurity;
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String save(@ModelAttribute("user") User user, Model model,
                       @ModelAttribute("password") String password) {

        logger.info("User {} entered save method", user.getEmail());

        user.setPass_bcrypt(password.toCharArray());
        userService.save(user);
        setAuthenticationInContextHolder(user);
        logger.info("User {} was saved!", user.getEmail());

        return "welcome";
    }

    public void setAuthenticationInContextHolder(User user) {
        UsernamePasswordAuthenticationToken authReq =
                new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPass_bcrypt());
        SecurityContextHolder.getContext().setAuthentication(authReq);
    }


}
