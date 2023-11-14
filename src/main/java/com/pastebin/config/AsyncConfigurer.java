package com.pastebin.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@EnableAsync
public class AsyncConfigurer {
    Logger logger = LoggerFactory.getLogger(AsyncConfigurer.class);

    @Bean
    public Executor executor() {

        ExecutorService executorService = Executors.newCachedThreadPool();

        logger.debug("Logger was created: {}", executorService);

        return executorService;
    }
}
