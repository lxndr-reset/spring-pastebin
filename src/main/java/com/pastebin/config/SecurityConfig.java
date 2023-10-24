package com.pastebin.config;

import com.pastebin.service.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final HandlerMappingIntrospector introspector;
    private final UserDetailsService userDetailsService;


    @Autowired
    public SecurityConfig(HandlerMappingIntrospector handlerMappingIntrospector, UserDetailsService userDetailsService) {
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
    public AuthenticationManager authenticationManager(HttpSecurity httpSecurity) {
        AuthenticationManagerBuilder builder = httpSecurity.getSharedObject(AuthenticationManagerBuilder.class);

        try {
            builder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
            return builder.build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Bean
    public MvcRequestMatcher.Builder mvc() {
        return new MvcRequestMatcher.Builder(getHandlerMappingIntrospector());
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(BCryptPasswordEncoder.BCryptVersion.$2B);
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);

        return daoAuthenticationProvider;
    }

    @Bean
    public AuthenticationProvider authenticationProvider(PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }


    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new SimpleUrlAuthenticationFailureHandler();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity, AuthenticationFailureHandler authenticationFailureHandler,
                                           AuthenticationConfiguration authenticationConfiguration) throws Exception {
        httpSecurity.authorizeHttpRequests(urlPatterns -> urlPatterns
                        .requestMatchers(
                                mvc().pattern("/admin/**")
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                mvc().pattern("/message/get/all")
                        ).hasAuthority("USER")
                        .anyRequest().permitAll())
                .sessionManagement(sessions -> {
                    sessions.maximumSessions(1);
                })
                .authenticationManager(authenticationConfiguration.getAuthenticationManager())
                .logout(logout -> {
                    logout.deleteCookies("JSESSIONID", "remove");
                    logout.logoutRequestMatcher(mvc().pattern("/logout"));
                    logout.logoutUrl("/logout").clearAuthentication(true);
                    logout.invalidateHttpSession(true);
                    logout.logoutSuccessUrl("/");
                })
//                .formLogin(form -> {
//                    try {
//                        form.loginPage("/login");
//                        form.failureHandler(authenticationFailureHandler);
//                    } catch (Exception e) {
//                        throw new RuntimeException(e);
//                    }
//                })
                .csrf(csrf -> {
                            try {
                                csrf.init(httpSecurity);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                );
        return httpSecurity.build();
    }
}
