package com.pastebin.aspect;

import com.pastebin.exception.NotAuthenticatedException;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class CheckIfUserLoggedIn {
    @Before("@annotation(com.pastebin.annotation.OnlyLoggedIn)")
    public void checkAuthentication() throws NotAuthenticatedException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new NotAuthenticatedException();
        }
    }}
