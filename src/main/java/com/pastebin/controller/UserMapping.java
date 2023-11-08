package com.pastebin.controller;

import com.pastebin.dto.UserDTO;
import com.pastebin.entity.User;
import com.pastebin.service.entityService.UserService;
import com.pastebin.service.user_details.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String save(@ModelAttribute("user_dto") UserDTO userDTO, Model model) {
        String email = userDTO.getEmail();
        User user = new User(email, new String(userDTO.getPassword()));

        userService.save(user); //throws exception if user is not unique
        UserDetails userDetails = userDetailsService.loadByUser(user);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities())
        );

        model.addAttribute("user", user);
        return "get_all_messages";
    }

}
