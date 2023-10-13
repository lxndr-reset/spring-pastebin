package com.pastebin.service;

import com.pastebin.entity.User;
import com.pastebin.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@Transactional
public class UserService {
    private final UserRepo userRepo;

    @Autowired
    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public User save(User user) {
        return userRepo.save(user);
    }

    public User findUserByEmail(String email) {
        return userRepo.findUserByEmail(email).orElseThrow(
                () -> new NoSuchElementException("User " + email + " not found")
        );
    }
}
