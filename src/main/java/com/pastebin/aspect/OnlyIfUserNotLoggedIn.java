package com.pastebin.aspect;

import com.pastebin.auth.AuthenticationContext;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class OnlyIfUserNotLoggedIn {
    private final AuthenticationContext authenticationContext;

    @Autowired
    public OnlyIfUserNotLoggedIn(AuthenticationContext authenticationContext) {
        this.authenticationContext = authenticationContext;
    }

    @Before("@annotation(com.pastebin.annotation.NotLoggedIn)")
    public void checkAuthenticationThrowExceptionIfTrue() throws Exception {
        if (authenticationContext.isUserAuthenticated()){
            throw new Exception("User already logged in");
        }
    }}
