package com.pastebin.entity;

import com.pastebin.entity.date.ValidTime;
import jakarta.persistence.*;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Objects;

import static jakarta.persistence.CascadeType.ALL;

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

    @Column(name = "delete_date")
    private Timestamp deletionDate;

    @OneToOne(mappedBy = "message", cascade = ALL
            , fetch = FetchType.LAZY)
    private ShortURL shortURL;

    public Message() {
    }

    public Message(String value, ShortURL url) {
        this.value = value;
        this.shortURL = url;
    }

    public Message(String value, ShortURL url, ValidTime deletionDate) {
        this.value = value;
        this.shortURL = url;
        setDeletionDate(deletionDate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message message)) return false;
        return Objects.equals(getId(), message.getId())
                && Objects.equals(getValue(), message.getValue())
                && Objects.equals(deleted, message.deleted)
                && Objects.equals(getShortURL(), message.getShortURL())
                && Objects.equals(deletionDate, message.deletionDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(deletionDate, getId(), getValue(), deleted, getShortURL());
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
                ", deleteDate='" + deletionDate + '\'' +
                ", value='" + value + '\'' +
                ", deleted=" + deleted +
                '}';
    }

    public Timestamp getDeletionDate() {
        return deletionDate;
    }

    public void setDeletionDate(Timestamp delete_date) {
        this.deletionDate = delete_date;
    }

    public void setDeletionDate(ValidTime delete_date) {
        this.deletionDate = delete_date.toTimeStamp();
    }
}