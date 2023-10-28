package com.pastebin.repository;

import com.pastebin.entity.Message;
import com.pastebin.entity.ShortURL;
import com.pastebin.entity.date.ValidTime;
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

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    void testDeleteByDeletedIsTrueOrExpired() {
        Message message1 = new Message("test1", Timestamp.from(Instant.now()), new ShortURL("a"));
        Message message2 = new Message("test2", new ShortURL("b"), ValidTime.ONE_HOUR);
        message2.setDeleted(true);

        Message message3 = new Message("test3", new ShortURL("c"), ValidTime.ONE_HOUR);
        Message message4 = new Message("test4", Timestamp.from(Instant.now()), new ShortURL("d"));
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