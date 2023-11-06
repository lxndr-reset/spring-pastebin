package com.pastebin.controller;

import com.pastebin.entity.Message;
import com.pastebin.entity.ShortURL;
import com.pastebin.entity.date.ValidTime;
import com.pastebin.service.entityService.MessageService;
import com.pastebin.service.entityService.ShortURLService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.concurrent.CompletableFuture;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@ActiveProfiles("local")
@SpringBootTest
class MessageMappingTest {
    @Autowired
    private MockMvc mockMVC;

    @MockBean
    private MessageService messageService;

    @MockBean
    private ShortURLService shortURLService;

    @Test
    void getRoot_whenRequestIsMade_thenReturnsHomePage() throws Exception {
        String COMPARE_RESPONSE_STRING = "http://localhost/users{?page,size,sort}";

        ResultActions responseActions = mockMVC.perform(get("/"));

        responseActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content()
                        .string(containsString(COMPARE_RESPONSE_STRING)
                        )
                );
    }

    @Test
    void getMessageByValue_whenRequest_thenReturnsMessagePage() throws Exception {
        ShortURL testShortURL = new ShortURL("a");
        CompletableFuture<Message> messageCompletableFuture = CompletableFuture.completedFuture(
                new Message("Test", testShortURL, ValidTime.THREE_MONTHS)
        );
        String urlValue = testShortURL.getUrlValue();

        when(messageService.findByShortURLValue(urlValue)).thenReturn(messageCompletableFuture);
        messageCompletableFuture.join();

        ResultActions resultActions = mockMVC.perform(get("/message/get/" + urlValue));

        resultActions.andExpect(status().isOk())
                .andExpect(content().string(containsString("message/get/" + urlValue)));
    }
}