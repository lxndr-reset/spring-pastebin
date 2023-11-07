package com.pastebin.service.entityService;

import com.pastebin.annotation.AvailableMessages;
import com.pastebin.entity.Message;
import com.pastebin.repository.MessageRepo;
import com.pastebin.service.user_details.UserDetails;
import org.eclipse.jdt.internal.compiler.ast.CastExpression;
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

    @AvailableMessages
    @Cacheable(value = "message")
    @Async
    public CompletableFuture<Message> findByShortURLValue(String value) {
        Optional<Message> retrievedOptionalMessage = messageRepo.findMessageByShortURLUrlValue(value);

        String exceptionMessage = "Message on link https://localhost:8080/message/get/" + value + " not found";

        if (retrievedOptionalMessage.isPresent()) {
            Message retrievedMessage = retrievedOptionalMessage.get();

            if (!retrievedMessage.isDeleted()) {
                return CompletableFuture.completedFuture(retrievedMessage);
            }
            exceptionMessage = "Message on link https://localhost:8080/message/get/" + value + " is deleted";
        }
        throw new NoSuchElementException(exceptionMessage);

    }

    @AvailableMessages
    @Async
    @Cacheable(value = "message", key = "#id")
    @CacheEvict(value = "messages", key = "#id")
    public CompletableFuture<Message> findById(Long id) {
        Optional<Message> message = messageRepo.findById(id);
        if (message.isEmpty() || message.get().isDeleted() || System.currentTimeMillis() >
                message.get().getDeletionDate().getTime()) {
            throw new NoSuchElementException("Message with id " + id + " not found");
        }
        return CompletableFuture.completedFuture(message.get());
    }

    @CacheEvict(value = {"message", "messages"}, key = "#value")
    @Async
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
            if (!message.isDeleted()) {
                message.setDeleted(true);
                messageRepo.save(message);
                return message;
            }
        }
        return null;
    }

    @CacheEvict(value = "message", key = "#id")
    @Async
    public CompletableFuture<Message> softDeleteById(Long id) {
        Message message = softDeletedMessageIfExists(messageRepo.findById(id));
        if (message != null) return CompletableFuture.completedFuture(message);

        throw new NoSuchElementException("Message on link https://localhost:8080/message/get/" + id + " does not exists");
    }


    @AvailableMessages
    @Caching(evict = @CacheEvict(value = "message", key = "#message.shortURL.urlValue"),
            put = @CachePut(value = "message", key = "#message.shortURL.urlValue"))
    @CacheEvict(value = "messages", key = "#message.shortURL.urlValue")
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

    @CacheEvict(value = "message", key = "#id")
    @Async
    public void deleteById(Long id) {
        messageRepo.deleteById(id);
    }

    @CacheEvict(value = "message", key = "#message.shortURL.urlValue")
    public void invalidateMessageCache(Message message) {
    }

    @CachePut(value = "message", key = "#message.shortURL.urlValue")
    public void validateMessageCache(Message message) {
    }

    @Async
    public void deleteAllByDeletedIsTrueOrDeletionDateIsGreaterThanEqual() {
        List<Message> messages = messageRepo.findAllByDeletedIsTrueOrDeletionDateIsLessThanEqual(
                new Timestamp(System.currentTimeMillis()));

        messages.forEach(this::invalidateMessageCache);

        messageRepo.deleteAllInBatch(messages);
    }

    public Set<Message> getMessagesByUser_Email(String email) {
        logger.trace("Custom Message getter was used!");

        return messageRepo.getMessagesByUser_Email(email);
    }
}
