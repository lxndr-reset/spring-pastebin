package com.pastebin.service;

import com.pastebin.entity.ShortURL;
import com.pastebin.util.ShortURLValueGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class ScheduledOperations {
    private final MessageService messageService;
    private final ShortURLService shortURLService;
    private boolean isDeletingRunning = false;

    @Autowired
    public ScheduledOperations(MessageService messageService, ShortURLService shortURLService) {
        this.messageService = messageService;
        this.shortURLService = shortURLService;
    }

    @Scheduled(fixedDelay = 14,initialDelay = 14, timeUnit = TimeUnit.DAYS)
    @Transactional
    @CacheEvict("message")
    public void finalDeleteMessages() {
        if (!isDeletingRunning) {
            isDeletingRunning = true;
            messageService.deleteAllMessagesByDeleted();
            isDeletingRunning = false;

            System.out.println(getDateTime() + "Messages were removed from database");
        }
    }

    /**
     * Generates link values based on the last generated amount of short URLs.
     * The method is scheduled to run with a fixed delay of 12 hours.
     * It is annotated with @Transactional to ensure data consistency during the generation process.
     *
     * @see ShortURLValueGenerator#generate(String, int)
     */
    @Scheduled(fixedDelay = 12, initialDelay = 12, timeUnit = TimeUnit.HOURS)
    @Transactional
    @Async
    public void generateLinkValue() { //todo add checking should links be generated or not
        int valuesToGenerateAmount = (int) Math.round(ShortURL.getLastGeneratedAmount() * ShortURL.getMultiplier());
        List<ShortURL> linkValues = new ArrayList<>();

        ShortURLValueGenerator.generate(ShortURL.getLastGeneratedValue(), valuesToGenerateAmount)
                .forEach(seq -> linkValues.add(new ShortURL(new String(seq))));

        System.out.println(getDateTime() + "Unique URLs were generated");

        ShortURL.setLastGeneratedValue(linkValues.getLast().getUrlValue());
        ShortURL.setLastGeneratedAmount(valuesToGenerateAmount);

        shortURLService.saveAll(linkValues);
    }
    /**
     * Returns the current time in the format "dd.MM.yyyy HH:mm:ss" and adds a double-dash.
     *
     * @return the current time as a string
     */
    private String getDateTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")) + " -- ";
    }
}
