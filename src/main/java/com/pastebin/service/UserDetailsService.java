package com.pastebin.service;

import com.pastebin.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {
    private final UserService userService;

    @Autowired
    public UserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userService.findUserByEmail(email);
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPass_bcrypt(), new ArrayList<>());
    }

    public UserDetails loadUserByEntity(User user) {
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPass_bcrypt(), new ArrayList<>());
    }
}
