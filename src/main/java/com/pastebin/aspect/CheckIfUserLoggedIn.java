package com.pastebin.aspect;

import com.pastebin.auth.AuthenticationStatus;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class CheckIfUserLoggedIn {
    private final AuthenticationStatus authenticationStatus;

    public CheckIfUserLoggedIn(AuthenticationStatus authenticationStatus) {
        this.authenticationStatus = authenticationStatus;
    }

    @Before("@annotation(com.pastebin.annotation.NotLoggedIn)")
    public void checkAuthenticationThrowExceptionIfTrue() {
        if (authenticationStatus.isUserAuthenticated()){
            throw new RuntimeException("User already logged in");
        }
    }}
