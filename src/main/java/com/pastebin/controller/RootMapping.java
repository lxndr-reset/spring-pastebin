package com.pastebin.controller;

import com.pastebin.annotation.NotLoggedIn;
import com.pastebin.dto.UserDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class RootMapping {

    @RequestMapping(value = "/register")
    @NotLoggedIn
    public String register(Model model) {
        model.addAttribute("user_dto", new UserDTO());

        return "registration";
    }

    @RequestMapping(value = "/login")
    @NotLoggedIn
    public String login(Model model) {
        model.addAttribute("user_dto", new UserDTO());

        return "login";
    }
}
