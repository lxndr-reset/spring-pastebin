package com.pastebin.dao;

import com.pastebin.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@Transactional
public class UserServiceImpl implements UserDetailsService {
    private final UserAccessRepo userAccessRepo;

    @Autowired
    public UserServiceImpl(UserAccessRepo userAccessRepo) {
        this.userAccessRepo = userAccessRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userAccessRepo.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException(username));

        return org.springframework.security.core.userdetails.User.withUsername(user.getEmail())
                .password(new String(user.getPassword())) //BCRYPT hash
                .authorities(Collections.emptyList())
                .build();
    }
}
