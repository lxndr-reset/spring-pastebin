package com.pastebin.service;

import com.pastebin.annotation.AvailableMessages;
import com.pastebin.entity.Message;
import com.pastebin.repository.MessageAccessRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class MessageService {
    private final MessageAccessRepo messageAccessRepo;

    @Autowired
    public MessageService(MessageAccessRepo messageAccessRepo) {
        this.messageAccessRepo = messageAccessRepo;
    }

    @AvailableMessages
    @Cacheable(value = "message", key = "#value")
    public Message findByValue(String value) {
        Optional<Message> byShortURLUrlValue = messageAccessRepo.findByShortURLUrlValue(value);

        if (byShortURLUrlValue.isPresent()) {
            Message message = byShortURLUrlValue.get();
            if (message != null && !message.isDeleted()) {
                return message;
            }
        }
        throw new NoSuchElementException("Message on link https://localhost:8080/get-message/" + value + " does not exists");
    }

    @AvailableMessages
    @Cacheable(value = "message", key = "#id")
    public Message findById(Long id) {
        Optional<Message> message = messageAccessRepo.findById(id);
        if (message.isEmpty() || message.get().isDeleted() || System.currentTimeMillis() >
                message.get().getDeletionDate().getTime()) {
            throw new NoSuchElementException("Message with id " + id + " not found");
        }
        return message.get();
    }

    @CacheEvict(value = "message", key = "#value")
    public Message softDeleteByValue(String value) {
        Optional<Message> byShortURLUrlValue = messageAccessRepo.findByShortURLUrlValue(value);

        Message message = deletedMessageIfExists(byShortURLUrlValue);
        if (message != null) return message;

        throw new NoSuchElementException("Message on link https://localhost:8080/get-message/" + value + " does not exists");
    }

    private Message deletedMessageIfExists(Optional<Message> byShortURLUrlValue) {
        if (byShortURLUrlValue.isPresent()) {
            Message message = byShortURLUrlValue.get();
            if (message != null && !message.isDeleted()) {
                message.setDeleted(true);
                messageAccessRepo.save(message);
                return message;
            }
        }
        return null;
    }

    @CacheEvict(value = "message", key = "#id")
    public Message softDeleteById(Long id) {
        Optional<Message> byShortURLUrlValue = messageAccessRepo.findById(id);

        Message message = deletedMessageIfExists(byShortURLUrlValue);
        if (message != null) return message;

        throw new NoSuchElementException("Message on link https://localhost:8080/get-message/" + id + " does not exists");
    }


    @AvailableMessages
    @CachePut(value = "message", key = "#message.shortURL.urlValue")
    public Message save(Message message) {
        return messageAccessRepo.save(message);
    }

    @CacheEvict(value = "message", key = "#id")
    public void deleteById(Long id) {
        messageAccessRepo.deleteById(id);
    }

    @Transactional
    public void deleteAllMessagesByDeleted() {
        messageAccessRepo.deleteAllByDeletedIsTrue().forEach(this::invalidateMessageCache);
    }

    @CacheEvict(value = "message", key = "#message.shortURL.urlValue")
    public void invalidateMessageCache(Message message) {
    }

    @Cacheable(value = "message", key = "#message.shortURL.urlValue")
    public void validateMessageCache(Message message) {
    }

    public void deleteAllByDeletedIsTrueOrDeletionDateIsGreaterThanEqual() {
        messageAccessRepo.deleteAllByDeletedIsTrueOrDeletionDateIsGreaterThanEqual(new Timestamp(System.currentTimeMillis()));
    }
}
