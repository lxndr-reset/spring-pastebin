package com.pastebin.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pastebin.util.ShortURLGenerationMetadata;
import jakarta.persistence.*;

import java.util.Objects;

import static jakarta.persistence.CascadeType.*;

@Entity
@Table(name = "short_url")
public class ShortURL {
    private static final double MULTIPLIER = 1.2;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "url_id")
    private Long urlId;

    @Column(name = "url_value")
    private String urlValue;

    @JoinColumn(name = "foreign_message_id", referencedColumnName = "message_id")
    @OneToOne(cascade = {PERSIST, DETACH, MERGE, REFRESH}, fetch = FetchType.LAZY)
    @JsonIgnore
    private Message message;

    public ShortURL() {
    }

    public ShortURL(String urlValue) {
        this.urlValue = urlValue;
    }

    public ShortURL(String urlValue, Message message) {
        this.urlValue = urlValue;
        this.message = message;
        message.setShortURL(this);
    }

    public static long getLastGeneratedAmount() {
        return ShortURLGenerationMetadata.getGenerationValue();
    }

    public static String getLastGeneratedValue() {
        return ShortURLGenerationMetadata.getLastGeneratedSequence();
    }

    public static double getMultiplier() {
        return MULTIPLIER;
    }

    @PreRemove
    public void preRemove() {
        this.setMessage(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ShortURL shortURL)) return false;
        return Objects.equals(urlId, shortURL.urlId) && Objects.equals(urlValue, shortURL.urlValue)
                && Objects.equals(message.getId(), shortURL.message.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(urlId, urlValue, message.getId());
    }

    public Long getUrlId() {
        return urlId;
    }

    public void setUrlId(Long urlId) {
        this.urlId = urlId;
    }

    public String getUrlValue() {
        return urlValue;
    }

    public void setUrlValue(String urlValue) {
        this.urlValue = urlValue;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ShortURL{" +
                "urlId=" + urlId +
                ", urlValue='" + urlValue + '\'' +
                '}';
    }
}
