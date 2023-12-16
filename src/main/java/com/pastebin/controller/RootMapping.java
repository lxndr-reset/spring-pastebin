package com.pastebin.controller;

import com.pastebin.dto.UserDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RootMapping {

    /**
     * Registers a new user.
     * <p>
     * This method is used to handle the registration process for a new user. It sets up the necessary model attributes
     * and returns the registration view name to be rendered.
     * <p>
     * After registration, you will be redirected on login page, where you can use registered credentials
     *
     * @param model the model object used to pass data to the view
     * @return the view name for the registration page
     */
    @GetMapping(value = "/register")
    public String register(Model model) {
        model.addAttribute("user_dto", new UserDTO());

        return "registration";
    }
}
