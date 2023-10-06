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

//    @GeneratedValue(strategy = GenerationType.AUTO)
//    @JoinColumn(name = "uid", referencedColumnName = "user_id") //todo uid is temporary deleted
//    private Integer userId;

    public Message() {
    }

    public Message(String message, ValidTime timestamp
//            , Integer uid
    ) {
        this.setMessage(message);

        LocalDateTime ldt = LocalDateTime.now().plusHours(timestamp.getHoursDuration());
        this.deletionDate = Timestamp.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
//        this.userId = uid;
    }

    public Message(String message) {
        this.setMessage(message);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message that)) return false;

        return
//                Objects.equals(userId, that.userId) &&
                Objects.equals(messageHashId, that.messageHashId)
                && Objects.equals(getMessage(), that.getMessage())
                && Objects.equals(getDeletionDate(), that.getDeletionDate())
                && Objects.equals(isDeleted, that.isDeleted);
    }

    @Override
    public int hashCode() {
        return Objects.hash(messageHashId, getMessage(), getDeletionDate(), isDeleted
//                , userId
        );
    }


    public void setMessage(String message) {
        try {
            this.messageHashId = new String((MessageDigest.getInstance("SHA-256").digest(message.getBytes(UTF_8))));
            this.message = message;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public String getMessageHashId() {
        return messageHashId;
    }

    public String getMessage() {
        return message;
    }

    public Timestamp getDeletionDate() {
        return deletionDate;
    }

    public Boolean getIsDeleted() {
        return isDeleted; // todo do that all deleting will just mark messages as deleted, and later we will delete them with a planned task
    }

    public void setIsDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

//    public int getUserId() {
//        return userId;
//    }
}
