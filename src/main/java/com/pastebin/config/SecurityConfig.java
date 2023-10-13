package com.pastebin.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends SecurityConfigurerAdapter {
    private final HandlerMappingIntrospector introspector;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    @Autowired
    public SecurityConfig(HandlerMappingIntrospector handlerMappingIntrospector,
                          AuthenticationManagerBuilder authenticationManagerBuilder) {
        this.introspector = handlerMappingIntrospector;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
    }

    public HandlerMappingIntrospector getIntrospector() {
        return introspector;
    }

    @Bean
    MvcRequestMatcher.Builder mvc() {
        return new MvcRequestMatcher.Builder(getIntrospector());
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(BCryptPasswordEncoder.BCryptVersion.$2B);
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(urlPatterns -> urlPatterns
                        .requestMatchers(
                                mvc().pattern("/admin")
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                mvc().pattern("/message/get/all")
                        ).authenticated()

                        .anyRequest().anonymous()

                )
                .authenticationManager(authenticationManagerBuilder.getOrBuild())
                .logout(logout -> {
                    logout.deleteCookies("JSESSIONID", "remove");
                    logout.logoutUrl("/logout");
                    logout.logoutSuccessUrl("/");
                })
                .formLogin(Customizer.withDefaults())
                .csrf(csrf -> {
                    try {
                        csrf.init(httpSecurity);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });

        return httpSecurity.build();
    }
}
