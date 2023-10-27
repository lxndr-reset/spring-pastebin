package com.pastebin.service;

import com.pastebin.entity.User;
import com.pastebin.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@Transactional
public class UserService {
    private final UserRepo userRepo;
    private final PasswordEncoder encoder;

    @Autowired
    public UserService(UserRepo userRepo, PasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.encoder = encoder;
    }

    @CachePut(value = "user", key = "#user.email")
    public User save(User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        return userRepo.save(user);
    }

    @Cacheable(value = "user", key = "#email")
    public User findUserByEmail(String email) {
        return userRepo.findUserByEmail(email).orElseThrow(
                () -> new NoSuchElementException("User " + email + " not found")
        );
    }
}
