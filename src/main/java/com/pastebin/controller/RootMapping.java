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

    //    @RequestMapping(value = "/login")
//    public String login(RedirectAttributes redirectAttributes, @PathVariable String email, @PathVariable String password) {
//        User userByEmail = userService.findUserByEmail(email);
//        redirectAttributes.addAttribute("user", userByEmail);
//        redirectAttributes.addAttribute("email", userByEmail.getEmail());
//
//        if (passwordEncoder.matches(password, userByEmail.getPass_bcrypt())) {
//            logger.info("auth for user {} successful", userByEmail.getEmail());
//            return "redirect:/message/get/all";
//        }
//
//        throw new NoSuchElementException("Wrong details. Try again");
//    }
}
