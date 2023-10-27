package com.pastebin.controller;

import com.pastebin.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;
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
            SecurityException.class, AccessDeniedException.class, AccessDeniedException.class, ExecutionException.class,
            AccessDeniedException.class
    })
    public ModelAndView handleError(ModelAndView mav, Exception exception) {
        addExceptionAttributesToModel((Model) mav.getModelMap(), exception);

        mav.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        mav.setViewName("error_page");
        return mav;
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
    public ModelAndView handleOtherExceptions(ModelAndView mav, Exception exception) {
        addExceptionAttributesToModel((Model) mav.getModelMap(), exception);
        mav.setViewName("error_page");

        LOGGER.error("Unknown error occurred", exception);

        return mav;
    }
}