package com.pastebin.config;


import com.github.benmanes.caffeine.cache.Caffeine;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableWebMvc
@EnableCaching
@PropertySource("classpath:application-${spring.profiles.active}.properties")
public class AppConfig {
    private final Executor executor;

    @Value("${spring.datasource.url}")
    private String dataSourceUrl;

    @Value("${spring.datasource.username}")
    private String dataSourceUsername;

    @Value("${spring.datasource.password}")
    private String dataSourcePassword;


    @Autowired
    public AppConfig(Executor executor) {
        this.executor = executor;
    }

    @Bean
    public CacheManager cacheManager() {

        Caffeine<Object, Object> caffeine = Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .executor(this.executor);

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
