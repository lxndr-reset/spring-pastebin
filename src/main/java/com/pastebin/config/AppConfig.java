package com.pastebin.config;


import com.github.benmanes.caffeine.cache.Caffeine;
import com.zaxxer.hikari.HikariDataSource;
import org.hibernate.property.access.spi.PropertyAccessBuildingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.sql.DataSource;
import java.awt.color.ProfileDataException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Configuration
@EnableWebMvc
@EnableCaching
@EnableAsync
public class AppConfig {

    private final Environment environment;

    @Autowired
    public AppConfig(Environment environment) {
        this.environment = environment;
    }

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
        Caffeine<Object, Object> caffeine = Caffeine.newBuilder().expireAfterWrite(10, TimeUnit.MINUTES)
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
        String propertyFileName = "application-local.properties"; //default

        try {
            if (containsProperty("local")) {
                propertyFileName = "application-local.properties";
            } else if (containsProperty("docker")) {
                propertyFileName = "application-docker.properties";
            } else {
                throw new ProfileDataException("Not contains 'local' or 'docker' profile");
            }

            properties = PropertiesLoaderUtils.loadAllProperties(propertyFileName);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load properties from " + propertyFileName, e);
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

    private boolean containsProperty(String ... propertyNames) {
        HashSet<String> profiles = new HashSet<>(Arrays.asList(environment.getActiveProfiles()));
        for (String name : propertyNames) {
            if (profiles.contains(name)){
                return true;
            }
        }

        return false;
    }
}
