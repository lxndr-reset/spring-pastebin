package com.pastebin.controller;

import com.pastebin.dao.services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class ScheduledOperations {
    private final MessageService messageService;
    private boolean isDeletingRunning = false;

    @Autowired
    public ScheduledOperations(MessageService messageService) {
        this.messageService = messageService;
    }

    @Scheduled(fixedDelay = 14, timeUnit = TimeUnit.DAYS)
    private void finalDelete(){
        if (!isDeletingRunning){

            isDeletingRunning = true;
            messageService.deleteAllMessagesByDeleted();
            isDeletingRunning = false;
            System.out.println("deleted ёмаё");
        }
    }
}
