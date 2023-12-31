package com.pastebin.repository;

import com.pastebin.entity.Message;
import com.pastebin.entity.ShortURL;
import com.pastebin.entity.User;
import com.pastebin.date.ValidTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class MessageRepositoryTest {

    @Autowired
    private MessageRepo messageRepo;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindMessageByShortURLUrlValue() {
        Message message = new Message("Test1", new ShortURL("a"), ValidTime.THREE_MONTHS);
        messageRepo.saveAndFlush(message);

        Optional<Message> messageByShortURLUrlValue = messageRepo.findMessageByShortURLUrlValue("a");

        Assertions.assertEquals(messageByShortURLUrlValue.get(), message);
    }

    @Test
    void getMessagesByUserEmailTest(){
        User user = new User("test@test.test", "[protected]");

        Message msg1 = new Message("Test1", new ShortURL("a"), ValidTime.THREE_MONTHS);
        msg1.setDeleted(true);
        msg1.setUser(user);

        Message msg2 = new Message("Test2", new ShortURL("b"), ValidTime.THREE_MONTHS);
        msg2.setUser(user);

        Message msg3 = new Message("Test3", new ShortURL("c"), ValidTime.THREE_MONTHS);
        List<Message> messages = List.of(msg1, msg2, msg3);

        messageRepo.saveAllAndFlush(messages);
        Set<Message> messagesByUserEmail = messageRepo.getMessagesByUser_Email(user.getEmail());

        assertEquals(Set.of(msg2), messagesByUserEmail);
    }
    @Test
    void testDeleteByDeletedIsTrueOrExpired() {
        Message message1 = new Message("test1",  new ShortURL("a"), Timestamp.from(Instant.now()));
        Message message2 = new Message("test2", new ShortURL("b"), ValidTime.ONE_HOUR);
        message2.setDeleted(true);

        Message message3 = new Message("test3", new ShortURL("c"), ValidTime.ONE_HOUR);
        Message message4 = new Message("test4", new ShortURL("d"), Timestamp.from(Instant.now()));
        message4.setDeleted(true);

        List<Message> messageEntities = Arrays.asList(message1, message2, message3, message4);

        messageRepo.saveAll(messageEntities);

        assertEquals(4, messageRepo.count());

        List<Message> expiredOrDeletedMessages = messageRepo.findAllByDeletedIsTrueOrDeletionDateIsLessThanEqual(
                Timestamp.valueOf(LocalDateTime.now())
        );
        assertEquals(3, expiredOrDeletedMessages.size());
    }
}