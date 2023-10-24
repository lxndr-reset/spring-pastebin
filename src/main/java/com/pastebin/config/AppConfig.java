package com.pastebin.config;


import com.github.benmanes.caffeine.cache.Caffeine;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableWebMvc
@EnableCaching
@EnableAsync
public class AppConfig {

    @Bean
    public Executor executor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(Runtime.getRuntime().availableProcessors());
        executor.setThreadNamePrefix("CustomAsyncExecutor-");
        executor.initialize();

        return executor;
    }

    @Bean
    public CacheManager cacheManager() {
        Caffeine<Object, Object> caffeine = Caffeine.newBuilder().expireAfterWrite(30, TimeUnit.MINUTES)
                .executor(executor());

        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        caffeineCacheManager.setCaffeine(caffeine);

        return caffeineCacheManager;
    }

    @Bean
    public CacheManagerCustomizer<CaffeineCacheManager> caffeineCacheManagerCustomizer() {
        return caffeineCacheManager -> caffeineCacheManager.setAllowNullValues(true);
    }

    @Bean
    public DataSource dataSource() {
        Properties properties;
        try {
            properties = PropertiesLoaderUtils.loadAllProperties("application.properties");
        } catch (IOException e) {
            throw new RuntimeException("Failed to load properties from application.properties", e);
        }

        String dataSourcePassword = properties.getProperty("spring.datasource.password");
        String dataSourceUsername = properties.getProperty("spring.datasource.username");
        String dataSourceUrl = properties.getProperty("spring.datasource.url");

        if (dataSourcePassword == null || dataSourceUsername == null || dataSourceUrl == null) {
            throw new IllegalArgumentException("DataSource configuration is missing");
        }

        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(dataSourceUrl);
        dataSource.setPassword(dataSourcePassword);
        dataSource.setUsername(dataSourceUsername);

        //to check if connection available
        try (Connection connection = dataSource.getConnection()) {

        } catch (SQLException e) {
            throw new RuntimeException("Failed to establish DB connection", e);
        }

        return dataSource;
    }
}
