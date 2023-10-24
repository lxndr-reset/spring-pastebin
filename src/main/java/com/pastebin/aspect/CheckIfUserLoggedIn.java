package com.pastebin.aspect;

import com.pastebin.auth.AuthenticationStatus;
import com.pastebin.exception.NotAuthenticatedException;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.nio.file.AccessDeniedException;

@Aspect
@Component
public class CheckIfUserLoggedIn {
    private final AuthenticationStatus authenticationStatus;

    public CheckIfUserLoggedIn(AuthenticationStatus authenticationStatus) {
        this.authenticationStatus = authenticationStatus;
    }

    @Before("@annotation(com.pastebin.annotation.NotLoggedIn)")
    public void checkAuthentication() {
        if (authenticationStatus.isUserAuthenticated()){
            throw new RuntimeException("User already logged in");
        }
    }}
