package com.pastebin.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pastebin.annotation.EmailCheck;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "\"user\"")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "email")
    @EmailCheck
    private String email;

    @Column(name = "pass_bcrypt")
    @Size(min = 72, max = 72, message = "Password must be hashed in 72 chars")
    private String pass_bcrypt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Message> allUsersMessages;

    public User(String email, String pass_bcrypt) {
        this.email = email;
        this.pass_bcrypt = pass_bcrypt;
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

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPass_bcrypt() {
        return pass_bcrypt;
    }

    public void setPass_bcrypt(String pass_bcrypt) {
        this.pass_bcrypt = pass_bcrypt;
    }

    public Set<Message> getAllUsersMessages() {
        return allUsersMessages;
    }

    public void setAllUsersMessages(Set<Message> allUsersMessages) {
        this.allUsersMessages = allUsersMessages;
    }
}
