package com.pastebin.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.io.*;
import java.util.Objects;

@Entity
@Table(name = "short_url")
public class ShortURL {
    private static final double MULTIPLIER = 1.5;
    private static int maxValueLength = 0;
    private static long lastGeneratedAmount = 20000;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "url_id")
    private Long urlId;

    @Column(name = "url_value")
    private String urlValue;

    @JoinColumn(name = "foreign_message_id", referencedColumnName = "message_id")
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
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
    }

    public static long getLastGeneratedAmount() {
        return lastGeneratedAmount;
    }

    public static void setLastGeneratedAmount(long lastGeneratedAmount) {
        ShortURL.lastGeneratedAmount = lastGeneratedAmount;
    }

    public static String getLastGeneratedValue() {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader("src/main/resources/last_sequence.txt"))) {
            return bufferedReader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setLastGeneratedValue(String lastGeneratedValue) {
        try (BufferedWriter bufferedReader = new BufferedWriter(new FileWriter("src/main/resources/last_sequence.txt"))) {
            bufferedReader.write(lastGeneratedValue);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static int getMaxValueLength() {
        return maxValueLength;
    }

    public static void setMaxValueLength(int maxValueLength) {
        ShortURL.maxValueLength = maxValueLength;
    }

    public static double getMultiplier() {
        return MULTIPLIER;
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

}
