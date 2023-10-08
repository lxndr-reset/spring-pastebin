package com.pastebin;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoadTest {


//    private static void stringGenerator(StringBuilder string) {
//        StringBuilder copy = new StringBuilder(string.toString());
//
//        for (char aChar : CHARS) {
//            string = new StringBuilder(copy);
//            string.append(aChar);
//            if (string.length() == TARGET_LENGTH) {
//                RES.add(string.toString());
//            } else {
//                stringGenerator(new StringBuilder(string.toString()));
//            }
//        }
//    }

    public void stressTest() {
        String url = "http://localhost:8080/get-message?id=1";
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(12);
        HttpClient httpClient = HttpClient.newHttpClient();

        int i1 = 500_000;
        for (int i = 0; i < i1; i++) {
            if (i != 5 && i != 1) {
                int finalI = i;
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    try {
                        httpClient.send(HttpRequest.newBuilder().uri(URI.create(url)).build(), HttpResponse.BodyHandlers.ofString());
                        System.out.println("Request number " + finalI + " executed!");
                    } catch (IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }, executor);
                futures.add(future);
            }
        }

        System.out.println("Futures created!");
        long start = System.currentTimeMillis();
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        httpClient.close();
        System.out.println();
        System.out.println(i1 + " requests took " + (System.currentTimeMillis() - start) + " ms");
    }
}
