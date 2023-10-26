package com.pastebin.service;

import com.pastebin.annotation.AvailableMessages;
import com.pastebin.entity.Message;
import com.pastebin.repository.MessageRepo;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional
public class MessageService {
    private final MessageRepo messageRepo;
    private final Logger logger = LoggerFactory.getLogger(MessageService.class);

    @Autowired
    public MessageService(MessageRepo messageRepo) {
        this.messageRepo = messageRepo;
    }

    @AvailableMessages
    @Cacheable(value = "message", key = "#value", unless = "#result == null")
    public Message findByValue(String value) {
        Optional<Message> byShortURLUrlValue = messageRepo.findMessageByShortURLUrlValue(value);

        if (byShortURLUrlValue.isPresent()) {
            Message message = byShortURLUrlValue.get();
            if (message != null && !message.isDeleted()) {
                return message;
            }
        }
        throw new NoSuchElementException("Message on link https://localhost:8080/message/get/" + value + " does not exists");
    }

    @AvailableMessages
    @Cacheable(value = "message", key = "#id")
    public Message findById(Long id) {
        Optional<Message> message = messageRepo.findById(id);
        if (message.isEmpty() || message.get().isDeleted() || System.currentTimeMillis() >
                message.get().getDeletionDate().getTime()) {
            throw new NoSuchElementException("Message with id " + id + " not found");
        }
        return message.get();
    }

    @CacheEvict(value = "message", key = "#value")
    public Message softDeleteByValue(String value) {
        Optional<Message> byShortURLUrlValue = messageRepo.findMessageByShortURLUrlValue(value);

        Message message = deletedMessageIfExists(byShortURLUrlValue);
        if (message != null) return message;

        throw new NoSuchElementException("Message on link https://localhost:8080/message/get/" + value + " does not exists");
    }

    private Message deletedMessageIfExists(Optional<Message> byShortURLUrlValue) {
        if (byShortURLUrlValue.isPresent()) {
            Message message = byShortURLUrlValue.get();
            if (message != null && !message.isDeleted()) {
                message.setDeleted(true);
                messageRepo.save(message);
                return message;
            }
        }
        return null;
    }

    @CacheEvict(value = "message", key = "#id")
    public Message softDeleteById(Long id) {
        Optional<Message> byShortURLUrlValue = messageRepo.findById(id);

        Message message = deletedMessageIfExists(byShortURLUrlValue);
        if (message != null) return message;

        throw new NoSuchElementException("Message on link https://localhost:8080/message/get/" + id + " does not exists");
    }


    @AvailableMessages
    @Caching(evict = @CacheEvict(value = "message", key = "#message.shortURL.urlValue"),
            put = @CachePut(value = "message", key = "#message.shortURL.urlValue"))
    public Message save(Message message) {
        try {
            return messageRepo.save(message);
        } catch (Exception e) {
            invalidateMessageCacheValue(message);
            throw e;
        }
    }

    @CacheEvict(value = "message", key = "#id")
    public void deleteById(Long id) {
        messageRepo.deleteById(id);
    }

    @Transactional
    public void deleteAllMessagesByDeleted() {
        messageRepo.deleteAllByDeletedIsTrue().forEach(this::invalidateMessageCacheValue);
    }

    @CacheEvict(value = "message", key = "#message.shortURL.urlValue")
    public void invalidateMessageCacheValue(Message message) {
    }

    @Cacheable(value = "message", key = "#message.shortURL.urlValue")
    public void validateMessageCache(Message message) {
    }

    public void deleteAllByDeletedIsTrueOrDeletionDateIsGreaterThanEqual() {
        List<Message> messages = messageRepo.deleteAllByDeletedIsTrueOrDeletionDateIsGreaterThanEqual(
                new Timestamp(System.currentTimeMillis()));

        messages.forEach(this::invalidateMessageCacheValue);
    }
}
