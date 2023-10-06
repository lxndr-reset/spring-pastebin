package com.pastebin.dao.services;

import com.pastebin.dao.MessageAccessRepo;
import com.pastebin.entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class MessageService {
    private final MessageAccessRepo messageAccessRepo;

    @Autowired
    public MessageService(MessageAccessRepo messageAccessRepo) {
        this.messageAccessRepo = messageAccessRepo;
    }

    @Transactional(readOnly = true)
    public Message findById(Long id) {
        Optional<Message> message = messageAccessRepo.findById(id);
        if (message.isEmpty() || message.get().getDeleted()) {
            throw new NoSuchElementException("Message with id " + id + " not found");
        }
        return message.get();
    }

    @Transactional
    public Message save(Message message) {
        return messageAccessRepo.save(message);
    }

    @Transactional
    public void deleteById(Long id) {
        messageAccessRepo.deleteById(id);
    }

    @Transactional
    public void deleteAllMessagesByDeleted(){
        messageAccessRepo.deleteAllMessagesByDeleted();
    }

}
