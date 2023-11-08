package com.pastebin.service;

import com.pastebin.entity.Message;
import com.pastebin.entity.ShortURL;
import com.pastebin.date.ValidTime;
import com.pastebin.repository.MessageRepo;
import com.pastebin.service.entityService.MessageService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {
    static RestTemplate restTemplate;

    @InjectMocks
    MessageService messageService;

    @Mock
    MessageRepo messageRepo;

    @BeforeAll
    static void beforeAll() {
        restTemplate = new RestTemplate();
    }

    @Test
    void softDeleteByValue() {
        Message testMessage = new Message("Test1", new ShortURL("b"), ValidTime.THREE_MONTHS);
        String value = testMessage.getShortURL().getUrlValue();
        Optional<Message> optionalMessage = Optional.of(testMessage);

        when(messageRepo.findMessageByShortURLUrlValue(value)).thenReturn(optionalMessage);

        messageService.softDeleteByValue(value).join();

        verify(messageRepo, times(1)).findMessageByShortURLUrlValue(value);

        Exception exception = assertThrows(NoSuchElementException.class, () ->
                messageService.findByShortURLValue(value).join()
        );

        assertEquals("Message on link https://localhost:8080/message/get/" + value + " is deleted",
                exception.getMessage()
        );
    }
}