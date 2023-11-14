package com.pastebin.service.entity_service;

import com.pastebin.annotation.AvailableMessages;
import com.pastebin.entity.Message;
import com.pastebin.repository.MessageRepo;
import com.pastebin.service.user_details.UserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Service
@Transactional
public class MessageService {
    private final MessageRepo messageRepo;
    private final Logger logger = LoggerFactory.getLogger(MessageService.class);
    private final UserService userService;

    @Autowired
    public MessageService(MessageRepo messageRepo, UserService userService) {
        this.messageRepo = messageRepo;
        this.userService = userService;
    }

    /**
     * Finds a message by its short URL value.
     *
     * @param value the short URL value to search for
     * @return a CompletableFuture of the retrieved message if found
     * @throws NoSuchElementException if the message is not found or is deleted
     */
    @AvailableMessages
    @Cacheable(value = "message")
    @Async
    public CompletableFuture<Message> findByShortURLValue(String value) {
        Optional<Message> retrievedOptionalMessage = messageRepo.findMessageByShortURLUrlValue(value);

        String exceptionMessage = "Message on link https://localhost:8080/message/get/" + value + " not found";

        if (retrievedOptionalMessage.isPresent()) {
            Message retrievedMessage = retrievedOptionalMessage.get();

            if (!retrievedMessage.getDeleted()) {
                return CompletableFuture.completedFuture(retrievedMessage);
            }
            exceptionMessage = "Message on link https://localhost:8080/message/get/" + value + " is deleted";
        }
        throw new NoSuchElementException(exceptionMessage);

    }

    @Async
    @CacheEvict(value = {"message", "messages"})
    public CompletableFuture<Message> softDeleteByValue(String value) {
        Optional<Message> byShortURLUrlValue = messageRepo.findMessageByShortURLUrlValue(value);

        Message message = softDeletedMessageIfExists(byShortURLUrlValue);
        if (message != null) return CompletableFuture.completedFuture(message);
        else {
            throw new NoSuchElementException("Message on link https://localhost:8080/message/get/" + value + " does not exists");
        }
    }


    private Message softDeletedMessageIfExists(Optional<Message> byShortURLUrlValue) {
        if (byShortURLUrlValue.isPresent()) {
            Message message = byShortURLUrlValue.get();

            if (!message.getDeleted()) {
                message.setDeleted(true);
                messageRepo.save(message);
                return message;
            }
        }
        return null;
    }

    /**
     * Saves a message and attaches a user to the message if the user is authenticated.
     *
     * @param message The message to be saved.
     */
    @AvailableMessages
    @Caching(
            evict = @CacheEvict(value = {"message", "messages"}),
            put = @CachePut(value = "message")
    )
    public void save(Message message) {
        attachUserToMessageIfAuthenticated(message);

        messageRepo.save(message);
    }

    private void attachUserToMessageIfAuthenticated(Message message) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!isAuthenticated(authentication)) {
            return;
        }

        Object principal = authentication.getPrincipal();
        try {
            String email = ((UserDetails) principal).getUsername();
            message.setUser(userService.findUserByEmail(email).get()); //if user is logged in, he's already persists in db
        } catch (ClassCastException e) {
            logger.debug("user credentials is not default. User details: " + principal);
        }
    }

    private boolean isAuthenticated(Authentication authentication) {
        return authentication.isAuthenticated();
    }


    @CacheEvict(value = "message")
    public void invalidateMessageCache(Message message) {
    }

    @CachePut(value = "message")
    public void validateMessageCache(Message message) {
    }

    @Async
    public void deleteAllByDeletedIsTrueOrDeletionDateIsGreaterThanEqual() {
        List<Message> messages = messageRepo.findAllByDeletedIsTrueOrDeletionDateIsLessThanEqual(new Timestamp(System.currentTimeMillis()));

        messages.forEach(this::invalidateMessageCache);

        messageRepo.deleteAllInBatch(messages);
    }

    public Set<Message> getMessagesByUser_Email(String email) {
        return messageRepo.getMessagesByUser_Email(email);
    }
}
