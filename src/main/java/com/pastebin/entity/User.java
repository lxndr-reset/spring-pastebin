package com.pastebin.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.springframework.stereotype.Component;

import java.util.*;

@Entity
@Component
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id")
    private Long uid;

    @Column(name = "email")
    private String email;

    @Column(name = "is_blocked")
    private Boolean isBlocked;

    @Column(name = "password_bcrypt")
    private char[] password;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER
//            , mappedBy = "user"
    )
    @JsonIgnore
    private Set<Message> allMessages;

    public User(String email, char[] password) {
        isBlocked = false;
        allMessages = new HashSet<>();

        this.email = email;
        this.password = password;
    }

    public User() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User that)) return false;
        return Objects.equals(uid, that.uid) && Objects.equals(email, that.email)
                && Objects.equals(isBlocked, that.isBlocked) && Arrays.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid, email, isBlocked, Arrays.hashCode(password));
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getBlocked() {
        return isBlocked;
    }

    public void setBlocked(Boolean blocked) {
        isBlocked = blocked;
    }

    public char[] getPassword() {
        return password;
    }

    public void setPassword(char[] password) {
        this.password = password;
    }

    public Set<Message> getAllMessages() {
        return allMessages;
    }

    public void setAllMessages(HashSet<Message> allMessages) {
        this.allMessages = allMessages;
    }
}
