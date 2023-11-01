package com.pastebin.controller;

import com.pastebin.auth.AuthenticationContext;
import com.pastebin.dto.UserDTO;
import com.pastebin.entity.User;
import com.pastebin.service.entityService.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.NoSuchElementException;
import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserMapping {
    private final UserService userService;
    private final Logger logger = LoggerFactory.getLogger(UserMapping.class);
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationContext authenticationContext;

    @Autowired
    public UserMapping(UserService userService, PasswordEncoder passwordEncoder, AuthenticationContext authenticationContext) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationContext = authenticationContext;
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String save(@ModelAttribute("user_dto") UserDTO userDTO, Model model) {
        String email = userDTO.getEmail();
        User user = new User(email, new String(userDTO.getPassword()));

        userService.save(user);

        authenticateAndAddUserToModel(userDTO, model, user);

        return "welcome";
    }

    @RequestMapping(value = "/perform_login", method = RequestMethod.POST)
    public String login(@ModelAttribute("user_dto") UserDTO userDTO, Model model) {

        Optional<User> retrievedUser = userService.findUserByEmail(userDTO.getEmail());

        if (retrievedUser.isPresent()) {
            User user = retrievedUser.get();
            boolean matches = passwordEncoder.matches(new String(userDTO.getPassword()), user.getPassword());

            if (matches) {
                authenticateAndAddUserToModel(userDTO, model, user);

                return "welcome";
            }
        }

        throw new NoSuchElementException("Wrong credentials! Try again. http://localhost:8080/login");
    }

    private void authenticateAndAddUserToModel(UserDTO userDTO, Model model, User user) {
        authenticationContext.setUserAuthenticated(userDTO);
        model.addAttribute("user", user);
    }
}
