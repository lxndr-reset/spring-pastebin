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
    private Boolean deleted = false;

    @OneToOne (mappedBy = "message", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ShortURL shortURL;

    public Message(String value, ShortURL url) {
        this.value = value;
        this.shortURL = url;
    }

    public Message() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message message)) return false;
        return Objects.equals(getId(), message.getId())
                && Objects.equals(getValue(), message.getValue())
                && Objects.equals(deleted, message.deleted)
                && Objects.equals(getShortURL(), message.getShortURL());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getValue(), deleted, getShortURL());
    }

    public ShortURL getShortURL() {
        return shortURL;
    }

    public void setShortURL(ShortURL shortURL) {
        this.shortURL = shortURL;
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

    public Boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", value='" + value + '\'' +
                ", deleted=" + deleted +
                '}';
    }
}