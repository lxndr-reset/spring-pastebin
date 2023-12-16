package com.pastebin.controller;

import com.pastebin.dto.UserDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class RootMapping {

    /**
     * Registers a new user.
     * <p>
     * This method is used to handle the registration process for a new user. It sets up the necessary model attributes
     * and returns the registration view name to be rendered.
     * <p>
     * After registration, you will be redirected on login page, where you can use registered credentials
     *
     * @param modelAndView the model and view object used to pass data to the view
     * @return the view name for the registration page
     */
    @GetMapping(value = "/register")
    public ModelAndView register(ModelAndView modelAndView) {
        modelAndView.getModelMap().addAttribute("user_dto", new UserDTO());
        modelAndView.setViewName("registration");

        return modelAndView;
    }
}
