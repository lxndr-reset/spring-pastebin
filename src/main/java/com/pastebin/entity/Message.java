package com.pastebin.entity;

import com.google.common.annotations.VisibleForTesting;
import com.pastebin.date.ValidTime;
import jakarta.persistence.*;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Objects;

import static jakarta.persistence.CascadeType.*;

@Entity
@Table(name = "\"message\"")
@Component
public class Message {

    @Id
    @Column(name = "message_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Transient
    private String deletionDateText;

    @Column(name = "message_value")
    private String value;

    @Column(name = "is_deleted")
    private Boolean deleted = false;

    @Column(name = "delete_date")
    private Timestamp deletionDate;

    @OneToOne(mappedBy = "message", cascade = {DETACH, MERGE, PERSIST, REFRESH}
            , fetch = FetchType.LAZY)
    private ShortURL shortURL;

    @JoinColumn(name = "owner_id")
    @ManyToOne(fetch = FetchType.LAZY, cascade = {DETACH, PERSIST, REFRESH})
    private User user;

    public Message() {
    }

    public Message(String value, ShortURL url) {
        this.value = value;
        this.shortURL = url;
    }

    public Message(String value, ShortURL url, ValidTime deletionDate) {
        this.value = value;
        this.shortURL = url;
        url.setMessage(this);
        this.setDeletionDate(deletionDate);
    }

    public Message(String value, ShortURL url, String deletionDateText) {
        this.value = value;
        this.shortURL = url;
        url.setMessage(this);
        this.deletionDateText = deletionDateText;
    }

    @VisibleForTesting
    public Message(String value, ShortURL shortURL, Timestamp deletionDate) {
        this.value = value;
        this.deletionDate = deletionDate;
        shortURL.setMessage(this);
        this.shortURL = shortURL;
    }

    public String getDeletionDateText() {
        return deletionDateText;
    }

    public void setDeletionDateText(String deletionDateText) {
        this.deletionDateText = deletionDateText;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;

        user.getAllUsersMessages().add(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message message)) return false;
        return Objects.equals(getId(), message.getId())
                && Objects.equals(getValue(), message.getValue())
                && Objects.equals(deleted, message.deleted)
                && Objects.equals(getShortURL(), message.getShortURL())
                && Objects.equals(deletionDate, message.deletionDate)
                && Objects.equals(getUser(), message.getUser());
    }

    @Override
    public int hashCode() {
        return Objects.hash(deletionDate, getId(), getValue(), deleted, getShortURL(), getUser());
    }

    public ShortURL getShortURL() {
        return shortURL;
    }

    public void setShortURL(ShortURL shortURL) {
        this.shortURL = shortURL;
        shortURL.setMessage(this);
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
                ", deletionDate=" + deletionDate +
                ", shortURL=" + shortURL +
                ", user=" + user +
                '}';
    }

    public Timestamp getDeletionDate() {
        return deletionDate;
    }

    public void setDeletionDate(Timestamp delete_date) {
        this.deletionDate = delete_date;
    }

    /**
     * Sets the deletion date for the object.
     *
     * @param string the string representation of the deletion date.
     *               Must be a valid format recognized by the ValidTime.valueOf() method.
     *               For example, "2021-12-31T23:59:59".
     */
    public void setDeletionDate(String string) {
        this.deletionDate = ValidTime.valueOf(string).toTimeStamp();
    }

    public void setDeletionDate(ValidTime delete_date) {
        this.deletionDate = delete_date.toTimeStamp();
    }
}