package com.pastebin.dao;

import com.pastebin.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAccessRepo extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
