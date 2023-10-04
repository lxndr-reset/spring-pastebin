package com.pastebin.dao;

import com.pastebin.entity.Message;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class MessageServiceImpl {
    private final MessageAccessRepo messageAccessRepo;

    @Autowired
    public MessageServiceImpl(MessageAccessRepo messageAccess) {
        this.messageAccessRepo = messageAccess;
    }

    public Message editMessage(String messageId, String newValue) {
        Message message = messageAccessRepo.findById(messageId).orElseThrow(() -> new ResourceNotFoundException("Message" +
                "with hash " + messageId + " not found"));
        message.setMessage(newValue);

        return messageAccessRepo.save(message);
    }
}
