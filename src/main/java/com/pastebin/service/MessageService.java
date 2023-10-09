package com.pastebin.service;

import com.pastebin.entity.Message;
import com.pastebin.repository.MessageAccessRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Cacheable(value = "message", key = "#message.shortURL.urlValue")
    public Message findById(Long id) {
        Optional<Message> message = messageAccessRepo.findById(id);
        if (message.isEmpty() || message.get().isDeleted()) {
            throw new NoSuchElementException("Message with id " + id + " not found");
        }
        return message.get();
    }

    @CachePut(value = "message", key = "#message.shortURL.urlValue")
    public Message save(Message message) {
        return messageAccessRepo.save(message);
    }

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

    @Transactional
    public List<Message> findAll() {
        List<Message> all = messageAccessRepo.findAll();
        all.forEach(this::validateMessageCache);

        return all;
    }

    public long countByDeletedIsFalse() {
        return messageAccessRepo.countByDeletedIsFalse();
    }

    public long countAllMessages() {
        return messageAccessRepo.count();
    }


}
