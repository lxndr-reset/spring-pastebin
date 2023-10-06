package com.pastebin;

import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PastebinApplicationTests {

    @Test
    void contextLoads() {
//        ResponseEntity<String> response = null;
//        for (int i = 0; i < 20_000; i++) {
//            response = restTemplate.getForEntity(url, String.class);
//            System.out.println("request " + i +" succeed!");
//        }
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    }

}
