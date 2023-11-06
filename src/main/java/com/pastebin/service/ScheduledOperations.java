package com.pastebin.service;

import com.pastebin.entity.ShortURL;
import com.pastebin.service.entityService.MessageService;
import com.pastebin.service.entityService.ShortURLService;
import com.pastebin.util.ShortURLValueGenerator;
import org.hibernate.annotations.BatchSize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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

    @Scheduled(fixedDelay = 3, timeUnit = TimeUnit.DAYS)
    @Transactional
    @BatchSize(size = 500)
    public void finalDeleteMessages() {
        if (!isDeletingRunning) {
            isDeletingRunning = true;
            messageService.deleteAllByDeletedIsTrueOrDeletionDateIsGreaterThanEqual();
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

    protected void generateLinks() {
        int valuesToGenerateAmount = (int) Math.round(ShortURL.getLastGeneratedAmount() * ShortURL.getMultiplier());
        List<ShortURL> linkValues = Collections.synchronizedList(new ArrayList<>());

        try (ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())) {
            String lastGeneratedValue = ShortURL.getLastGeneratedValue();
            List<CompletableFuture<Void>> futures = ShortURLValueGenerator.generate(lastGeneratedValue,
                            valuesToGenerateAmount)
                    .stream()
                    .map(seq -> CompletableFuture.supplyAsync(() ->
                            new ShortURL(new String(seq)), executor).thenAcceptAsync(linkValues::add))
                    .toList();

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            executor.shutdown();
        }

        System.out.println(getDateTime() + "Unique URLs were generated");

        shortURLService.saveAll(linkValues);
    }

    private String getMaxString(List<ShortURL> list) {
        return list.stream().max(Comparator.comparing(ShortURL::getUrlValue)).orElseThrow(() ->
                        new RuntimeException("Error in generating unique links. Please contact administrator"))
                .getUrlValue();
    }

    protected boolean isEnoughLinksAvailable() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/last_generated_amount.txt"))) {
            long lastGeneratedAmount = Long.parseLong(reader.readLine());
            return lastGeneratedAmount >> 2 >= shortURLService.countAllByMessageNull();
        }
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
