package com.pastebin.entity;

import jakarta.persistence.*;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Entity
@Table(name = "message")
@Component
public class Message {
    @Id
    @Column(name = "message_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "message_value")
    private String value;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;
//    @Column(name = "owner_id")
//    private Integer ownerId;


    public Message(String value) {
        this.value = value;
    }

    public Message() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message message)) return false;
        return Objects.equals(getId(), message.getId()) && Objects.equals(getValue(), message.getValue()) && Objects.equals(isDeleted, message.isDeleted);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getValue(), isDeleted);
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", value='" + value + '\'' +
                ", isDeleted=" + isDeleted +
                '}';
    }
}