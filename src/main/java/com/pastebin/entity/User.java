package com.pastebin.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "\"user\"")
public class User {
    private static final Logger logger = LoggerFactory.getLogger(User.class);
    private static final PasswordEncoder encoder = new BCryptPasswordEncoder();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "email")
    private String email;

    @Column(name = "pass_bcrypt")
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Message> allUsersMessages;


    public User(String email, String rawPassword) {
        this.setPassword(rawPassword);
        this.setEmail(email);
    }

    public User() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return Objects.equals(userId, user.userId) && Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, email);
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", email='" + email + '\'' +
                '}';
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String s) {
        if (s == null || s.isEmpty() || !s.matches("^[a-zA-Z0-9._+-]+@[a-zA-Z0-9-._]+\\.[a-zA-Z]+$")) {
            throw new NoSuchElementException("Wrong details");
        }
        this.email = s;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String rawPass) {
        this.password = rawPass;
    }

    public Set<Message> getAllUsersMessages() {
        return allUsersMessages;
    }

    public void setAllUsersMessages(Set<Message> allUsersMessages) {
        this.allUsersMessages = allUsersMessages;
    }
}
