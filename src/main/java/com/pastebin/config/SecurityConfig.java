package com.pastebin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private static void initializeFormLogin(HttpSecurity httpSecurity, FormLoginConfigurer<HttpSecurity> httpSecurityFormLoginConfigurer) {
        try {
            httpSecurityFormLoginConfigurer.init(httpSecurity);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(BCryptPasswordEncoder.BCryptVersion.$2B);
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity.authorizeHttpRequests(authMatchers -> authMatchers
                .requestMatchers(r -> r.getHeader("X-Forwarded-Proto") != null)
                .authenticated()
                .anyRequest().permitAll()
        ).httpBasic(Customizer.withDefaults()).formLogin(formLoginCustomizer ->
                initializeFormLogin(httpSecurity, formLoginCustomizer)
        );
        return httpSecurity.build();
    }
}
