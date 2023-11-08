package com.pastebin.aspect;

import com.pastebin.entity.Message;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;

@Aspect
@Component
public class CheckMessageIsAvailable {
    @AfterReturning(pointcut = "@annotation(com.pastebin.annotation.AvailableMessages)", returning = "result")
    public void aroundAdvice(Message result) {

        if (result.getDeleted() || (result.getDeletionDate() != null
                && result.getDeletionDate().getTime() <= System.currentTimeMillis())) {
            throw new NoSuchElementException("Element by link http://localhost:8080/message/get/" + result.
                    getShortURL().getUrlValue() + " was not found");
        }
    }
}