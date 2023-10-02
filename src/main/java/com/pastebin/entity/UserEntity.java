package com.pastebin.entity;

import jakarta.persistence.*;
import org.springframework.stereotype.Component;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Component
@Table(name = "user")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id")
    private Integer uid;

    @Column(name = "email")
    private String email;

    @Column(name = "is_blocked")
    private Boolean isBlocked;

    @Column(name = "password")
    private String password;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<MessageEntity> allMessages;

    public UserEntity(String email, String password) {
        isBlocked = false;
        allMessages = new ArrayList<>();

        this.email = email;
        this.password = password;
    }

    public UserEntity() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserEntity that)) return false;
        return Objects.equals(uid, that.uid) && Objects.equals(email, that.email) && Objects.equals(isBlocked, that.isBlocked) && Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid, email, isBlocked, password);
    }
}
