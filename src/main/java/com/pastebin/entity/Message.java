package com.pastebin.entity;

import com.pastebin.entity.date.ValidTime;
import jakarta.persistence.*;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Objects;

import static java.nio.charset.StandardCharsets.UTF_8;

@Table(name = "text")
@Entity
@Component
public class Message {
    @Id
    @Column(name = "sha256_hash")
    private String messageHashId;

    @Column(name = "value")
    private String message;

    @Column(name = "deletion_date")
    private Timestamp deletionDate;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @GeneratedValue(strategy = GenerationType.AUTO)
    @JoinColumn(name = "uid", referencedColumnName = "user_id")
    private int uid;

//    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
//    @JoinColumn(name = "uid", referencedColumnName = "user_id")
//    private User user;

    public Message() {
    }

    public Message(String message, ValidTime timestamp, int uid) {
        this.setMessage(message);

        LocalDateTime ldt = LocalDateTime.now().plusHours(timestamp.getHoursDuration());
        this.deletionDate = Timestamp.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
        this.uid = uid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message that)) return false;

        return uid == that.uid && Objects.equals(messageHashId, that.messageHashId) && Objects.equals(getMessage(), that.getMessage()) && Objects.equals(getDeletionDate(), that.getDeletionDate()) && Objects.equals(isDeleted, that.isDeleted);
    }

    @Override
    public int hashCode() {
        return Objects.hash(messageHashId, getMessage(), getDeletionDate(), isDeleted, uid);
    }


    public void setMessage(String message) {
        try {
            this.messageHashId = Arrays.toString(MessageDigest.getInstance("SHA-256").digest(message.getBytes(UTF_8)));
            this.message = message;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public String getMessageHashId() {
        return messageHashId;
    }

    public void setMessageHashId(String messageHashId) {
        this.messageHashId = messageHashId;
    }

    public String getMessage() {
        return message;
    }

    public Timestamp getDeletionDate() {
        return deletionDate;
    }

    public void setDeletionDate(Timestamp deletionDate) {
        this.deletionDate = deletionDate;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }
}
