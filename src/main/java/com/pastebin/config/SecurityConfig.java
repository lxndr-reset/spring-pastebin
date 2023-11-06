package com.pastebin.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
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


    @Autowired
    public SecurityConfig(HandlerMappingIntrospector handlerMappingIntrospector) {
        this.introspector = handlerMappingIntrospector;
    }

    public HandlerMappingIntrospector getHandlerMappingIntrospector() {
        return introspector;
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }


    @Bean
    public MvcRequestMatcher.Builder mvc() {
        return new MvcRequestMatcher.Builder(getHandlerMappingIntrospector());
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new SimpleUrlAuthenticationFailureHandler();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        authenticationProvider.setUserDetailsService(userDetailsService);

        return authenticationProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity,
                                           AuthenticationConfiguration authenticationConfiguration) throws Exception {
        httpSecurity.authorizeHttpRequests(urlPatterns -> urlPatterns
                        .requestMatchers(
                                mvc().pattern("/admin/**")
                        ).hasRole("ADMIN")


//                        .requestMatchers(
//                                mvc().pattern("/message/get/all")
//                        ).authenticated()
                        .requestMatchers(
                                mvc().pattern("/login")
                        ).anonymous()

                        .anyRequest().permitAll())
                .sessionManagement(sessions -> {
                    sessions.maximumSessions(1);
                })
                .authenticationManager(authenticationConfiguration.getAuthenticationManager())
                .logout(logout -> {
                    logout.deleteCookies("JSESSIONID", "remove");
                    logout.logoutUrl("/logout").clearAuthentication(true);
                    logout.invalidateHttpSession(true);
                    logout.logoutSuccessUrl("/");

                })
                .formLogin(form -> {
                    try {
                        form.loginPage("/login");
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
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
