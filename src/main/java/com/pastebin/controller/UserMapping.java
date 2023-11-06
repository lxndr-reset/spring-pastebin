package com.pastebin.controller;

import com.pastebin.dto.UserDTO;
import com.pastebin.entity.User;
import com.pastebin.service.user_details.UserDetailsService;
import com.pastebin.service.entityService.UserService;
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
    private final PasswordEncoder passwordEncoder;

    private final UserDetailsService userDetailsService;

    @Autowired
    public UserMapping(UserService userService, PasswordEncoder passwordEncoder, UserDetailsService userDetailsService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String save(@ModelAttribute("user_dto") UserDTO userDTO, Model model) {
        String email = userDTO.getEmail();
        User user = new User(email, new String(userDTO.getPassword()));

        userService.save(user); //throws exception if user is not unique

        userDetailsService.loadByUser(user);

        model.addAttribute("user", user);
        return "welcome";
    }

    @RequestMapping(value = "/perform_login", method = RequestMethod.POST)
    public String login(@ModelAttribute("user_dto") UserDTO userDTO, Model model) {

        Optional<User> retrievedUser = userService.findUserByEmail(userDTO.getEmail());

        if (retrievedUser.isPresent()) {
            User user = retrievedUser.get();
            boolean matches = passwordEncoder.matches(new String(userDTO.getPassword()), user.getPassword());

            if (matches) {
                userDetailsService.loadByUser(user);

                model.addAttribute("user", user);
                return "welcome";
            }
        }

        throw new NoSuchElementException("Wrong credentials! Try again. http://localhost:8080/login");
    }

}
