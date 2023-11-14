package com.pastebin.controller;

import com.pastebin.dto.UserDTO;
import com.pastebin.entity.User;
import com.pastebin.service.entity_service.UserService;
import com.pastebin.service.user_details.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/user")
public class UserMapping {
    private final UserService userService;
    private final UserDetailsService userDetailsService;

    @Autowired
    public UserMapping(UserService userService, UserDetailsService userDetailsService) {
        this.userService = userService;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Sets the user authenticated in the security context.
     *
     * @param userDetails The details of the authenticated user.
     */
    private static void setUserAuthenticated(UserDetails userDetails) {

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities())
        );

    }

    /**
     * Saves a user in the system and sets the user as authenticated in the security context.
     *
     * @param userDTO The user data transfer object containing user information.
     * @param model   The model object to be populated with data for the view.
     * @return A redirect string to the login page.
     * @throws DataIntegrityViolationException If the user is not unique.
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String save(@ModelAttribute("user_dto") UserDTO userDTO, Model model) {

        String email = userDTO.getEmail();
        User user = new User(email, new String(userDTO.getPassword()));

        userService.save(user);
        UserDetails userDetails = userDetailsService.loadByUser(user);

        setUserAuthenticated(userDetails);

        model.addAttribute("user", user);
        return "redirect:/login";

    }

}
