package com.pastebin.service.entityService;

import com.pastebin.entity.User;
import com.pastebin.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class UserService {
    private final UserRepo userRepo;
    private final PasswordEncoder encoder;

    @Autowired
    public UserService(UserRepo userRepo, @Lazy PasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.encoder = encoder;
    }

    public User save(User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        return userRepo.save(user);
    }

    public Optional<User> findUserByEmail(String email) {
        return userRepo.findUserByEmail(email);
    }
}
