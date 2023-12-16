package com.pastebin.service;

import com.pastebin.entity.ShortURL;
import com.pastebin.service.entity_service.MessageService;
import com.pastebin.service.entity_service.ShortURLService;
import com.pastebin.util.ShortURLValueGenerator;
import org.hibernate.annotations.BatchSize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Component
public class ScheduledOperations {
    private final MessageService messageService;
    private final ShortURLService shortURLService;
    private final Logger logger = LoggerFactory.getLogger(ScheduledOperations.class);
    private boolean isDeletingRunning = false;

    @Autowired
    public ScheduledOperations(MessageService messageService, ShortURLService shortURLService) {
        this.messageService = messageService;
        this.shortURLService = shortURLService;
    }

    private static int getGenerationAmount() {
        return (int) ShortURL.getLastGeneratedAmount();
    }

    private static List<CompletableFuture<Void>> getListOfShortURlFutures(String lastGeneratedValue, int valuesToGenerate, List<ShortURL> linkValues) {
        List<CompletableFuture<Void>> futures = ShortURLValueGenerator.generate(lastGeneratedValue,
                        valuesToGenerate)
                .stream()
                .map(seq -> CompletableFuture.supplyAsync(() ->
                        new ShortURL(new String(seq))).thenAcceptAsync(linkValues::add))
                .toList();
        return futures;
    }

    /**
     * This method is scheduled to run at fixed intervals of 3 days. It is responsible for deleting messages from the database.
     * It is annotated with @Scheduled
     */
    @Scheduled(fixedDelay = 3, initialDelay = 3, timeUnit = TimeUnit.DAYS)
    @Transactional
    @BatchSize(size = 500)
    public void finalDeleteMessages() {

        if (!isDeletingRunning) {
            isDeletingRunning = true;
            messageService.deleteAllByDeletedIsTrueOrDeletionDateIsGreaterThanEqual();
            isDeletingRunning = false;

            logger.trace(getDateTime() + "Messages were removed from database");
        }
    }

    /**
     * Generates link values based on the last generated amount of short URLs.
     * The method is scheduled to run with a fixed delay of 12 hours.
     * It is annotated with @Transactional to ensure data consistency during the generation process.
     *
     * @see ShortURLValueGenerator#generate(String, int)
     */
    @Scheduled(fixedDelay = 12
            , initialDelay = 12
            , timeUnit = TimeUnit.HOURS)
    @Transactional
    public void generateLinkValue() {

        try {

            if (!isEnoughLinksAvailable()) {
                generateLinks();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void generateLinkValueWithoutCheck() {
        generateLinks();
    }

    public void generateLinks() {
        int valuesToGenerate = getGenerationAmount();
        List<ShortURL> linkValues = Collections.synchronizedList(new ArrayList<>());

        String lastGeneratedValue = ShortURL.getLastGeneratedValue();

        List<CompletableFuture<Void>> futures = getListOfShortURlFutures(
                lastGeneratedValue, valuesToGenerate, linkValues
        );

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        logger.info("Unique URLs were generated in amount of {}", valuesToGenerate);

        shortURLService.saveAll(linkValues);
    }


    private boolean isEnoughLinksAvailable() throws IOException {
        return getGenerationAmount() >> 2 >= shortURLService.countAllByMessageNull();
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
