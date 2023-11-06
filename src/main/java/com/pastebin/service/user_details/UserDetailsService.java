package com.pastebin.service.user_details;

import com.pastebin.entity.User;
import com.pastebin.service.entityService.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {
    private final UserService userService;

    @Autowired
    public UserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userService.findUserByEmail(email).orElseThrow(() -> new NoSuchElementException("User not found"));

        return new com.pastebin.service.user_details.UserDetails(user);
    }

    public UserDetails loadByUser(User user) throws UsernameNotFoundException {
        userService.findUserByEmail(user.getEmail()).orElseThrow(() -> new NoSuchElementException("User not found"));

        return new com.pastebin.service.user_details.UserDetails(user);
    }
}