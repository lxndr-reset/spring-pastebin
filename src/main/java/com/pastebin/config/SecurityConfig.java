package com.pastebin.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.session.ConcurrentSessionControlAuthenticationStrategy;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final HandlerMappingIntrospector introspector;
    private final com.pastebin.service.user_details.UserDetailsService userDetailsService;

    @Autowired
    public SecurityConfig(HandlerMappingIntrospector handlerMappingIntrospector, com.pastebin.service.user_details.UserDetailsService userDetailsService) {
        this.introspector = handlerMappingIntrospector;
        this.userDetailsService = userDetailsService;
    }

    public HandlerMappingIntrospector getHandlerMappingIntrospector() {
        return introspector;
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public ConcurrentSessionControlAuthenticationStrategy concurrentSessionControlAuthenticationStrategy() {

        ConcurrentSessionControlAuthenticationStrategy authenticationStrategy =
                new ConcurrentSessionControlAuthenticationStrategy(sessionRegistry());

        authenticationStrategy.setMaximumSessions(1);

        return authenticationStrategy;
    }

    @Bean
    public MvcRequestMatcher.Builder mvc() {
        return new MvcRequestMatcher.Builder(
                getHandlerMappingIntrospector()
        );
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new SimpleUrlAuthenticationFailureHandler();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();

        authenticationProvider.setPasswordEncoder(passwordEncoder);
        authenticationProvider.setUserDetailsService(userDetailsService);

        return authenticationProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity, AuthenticationProvider authenticationProvider)
            throws Exception {

        httpSecurity.authorizeHttpRequests(urlPatterns -> urlPatterns
                        .requestMatchers(
                                mvc().pattern("/messages/**"),
                                mvc().pattern("/shorturls/**"),
                                mvc().pattern("/users/**")
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                mvc().pattern("/message/get/all")
                        ).authenticated()
                        .requestMatchers(
                                mvc().pattern("/login")
                        ).anonymous()

                        .anyRequest().permitAll())
                .sessionManagement(sessions -> {
                    sessions.maximumSessions(1);
                    sessions.sessionAuthenticationStrategy(concurrentSessionControlAuthenticationStrategy());
                })

                .authenticationProvider(authenticationProvider)
                .logout(Customizer.withDefaults())
                .formLogin(Customizer.withDefaults())
                .csrf(Customizer.withDefaults());

        return httpSecurity.build();
    }
}
