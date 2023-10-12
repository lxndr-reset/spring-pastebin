package com.pastebin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class UserMappingController {
    @RequestMapping(name = "/register")
    public String register(){
        return "register";
    }
}
