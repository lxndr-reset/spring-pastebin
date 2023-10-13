package com.pastebin.controller;

import com.pastebin.entity.User;
import com.pastebin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class UserMappingController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserMappingController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @RequestMapping(value = "/register/{email}/{password}")
    public String register(Model model, @PathVariable String email, @PathVariable String password) {
        User save = userService.save(new User(email, passwordEncoder.encode(password)));
        model.addAttribute("user", save);

        return "register";
    }
}
