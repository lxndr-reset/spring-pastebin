package com.pastebin.controller;

import com.pastebin.entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class RootMapping {

    @RequestMapping(value = "/register")
    public String register(Model model) {
        model.addAttribute("user", new User());

        return "registration";
    }

    @RequestMapping(value = "/login")
    public String login(Model model) {
        model.addAttribute("user", new User());

        return "/login";
    }
}
