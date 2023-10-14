package com.pastebin.controller;

import com.pastebin.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NotAuthenticatedException.class)
    public String notAuthenticated() {
        return "redirect:/login";
    }

    @ExceptionHandler({NoSuchElementException.class, MessageDeletedException.class,
            NoAvailableShortURLException.class, UrlNotExistsException.class,
            UserBlockedException.class, IllegalArgumentException.class,
            SecurityException.class, AccessDeniedException.class, AccessDeniedException.class
    })
    public String handleError(Model model, Exception exception) {
        addExceptionAttributesToModel(model, exception);

        LOGGER.error("Caught exception", exception);

        return "error_page";
    }

    private void addExceptionAttributesToModel(Model model, Exception exception) {
        List<String> stackTrace = Arrays.stream(exception.getStackTrace())
                .map(StackTraceElement::toString)
                .collect(Collectors.toList());
        model.addAttribute("message", exception.getMessage());
        model.addAttribute("currentTime", LocalDateTime.now().toString());
        model.addAttribute("exceptionName", exception.getClass().getName());
        model.addAttribute("stack_trace", stackTrace);
    }

    @ExceptionHandler(Exception.class)
    public String handleOtherExceptions(Model model, Exception exception) {
        addExceptionAttributesToModel(model, exception);

        LOGGER.error("Unknown error occurred", exception);

        return "error_page";
    }
}