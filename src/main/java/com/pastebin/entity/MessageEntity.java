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
public class MessageEntity {
    @Id
    @Column(name = "sha256_hash")
    private String sha256hash;

    @Column(name = "value")
    private String message;

    @Column(name = "deletion_date")
    private Timestamp deletionDate;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @GeneratedValue(strategy = GenerationType.AUTO)
    @JoinColumn(name = "uid", referencedColumnName = "user_id")
    private int uid;

    public MessageEntity() {
    }

    public MessageEntity(String message, ValidTime timestamp, int uid) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(message.getBytes());
            this.sha256hash = Arrays.toString(md.digest());
            md.reset();
            this.message = message;

            LocalDateTime ldt = LocalDateTime.now().plusHours(timestamp.getHoursDuration());
            this.deletionDate = Timestamp.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
            this.uid = uid;

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MessageEntity that)) return false;

        return uid == that.uid && Objects.equals(sha256hash, that.sha256hash) && Objects.equals(getMessage(), that.getMessage()) && Objects.equals(getDeletionDate(), that.getDeletionDate()) && Objects.equals(isDeleted, that.isDeleted);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sha256hash, getMessage(), getDeletionDate(), isDeleted, uid);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        try {
            this.sha256hash = Arrays.toString(MessageDigest.getInstance("sha256").digest(message.getBytes(UTF_8)));
            this.message = message;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public Timestamp getDeletionDate() {
        return deletionDate;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }
}
