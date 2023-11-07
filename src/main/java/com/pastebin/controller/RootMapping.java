package com.pastebin.controller;

import com.pastebin.dto.UserDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class RootMapping {

    @RequestMapping(value = "/register")
    public String register(Model model) {
        model.addAttribute("user_dto", new UserDTO());

        return "registration";
    }
}
