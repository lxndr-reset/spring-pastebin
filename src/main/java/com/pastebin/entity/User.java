package com.pastebin.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "email")
    private String email;

    @Column(name = "pass_bcrypt")
    private String pass_bcrypt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Message> allUsersMessages;

    public User(Integer userId, String email, String pass_bcrypt) {
        this.userId = userId;
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

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
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
