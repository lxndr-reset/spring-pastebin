package com.pastebin.aspect;

import com.pastebin.entity.Message;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;

@Aspect
@Component
public class CheckMessageIsExpired {
    @AfterReturning(pointcut = "@annotation(com.pastebin.annotation.AvailableMessages)", returning = "result")
    public void aroundAdvice(Message result) {

        if (result.isDeleted() || (result.getDeletionDate() != null
                && result.getDeletionDate().getTime() <= System.currentTimeMillis())) {
            throw new NoSuchElementException("Element with link http://localhost:8080/get-message/" + result.
                    getShortURL().getUrlValue() + " was not found");
        }
    }
}