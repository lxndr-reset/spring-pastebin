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
import java.util.stream.Collectors;
import java.util.concurrent.ExecutionException;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final String ERROR_VIEW_NAME = "error_page";

    @ExceptionHandler(NotAuthenticatedException.class)
    public String notAuthenticated() {
        return "redirect:/login";
    }

    @ExceptionHandler({NoSuchElementException.class, MessageDeletedException.class,
            NoAvailableShortURLException.class, UrlNotExistsException.class,
            UserBlockedException.class, IllegalArgumentException.class,
            SecurityException.class, AccessDeniedException.class, ExecutionException.class,
            Exception.class //
    })
    public ModelAndView handleError(Model model, Exception exception) {

        ModelAndView mav = new ModelAndView();

        if(exception != null){
            LOGGER.error("Unknown error occurred", exception);
        }

        assert exception != null;
        addExceptionAttributesToModel(model, exception);

        mav.setViewName(ERROR_VIEW_NAME);
        mav.setStatus(getStatusCodeByExceptionType(exception));

        return mav;
    }

    private void addExceptionAttributesToModel(Model model, Exception exception) {

        model.addAttribute("message", exception.getMessage());
        model.addAttribute("currentTime", LocalDateTime.now().toString());
        model.addAttribute("exceptionName", exception.getClass().getName());
        model.addAttribute("stack_trace", convertStackTraceToString(exception.getStackTrace()));

    }

    private HttpStatus getStatusCodeByExceptionType(Exception exception) {

        if (exception instanceof AccessDeniedException || exception instanceof UserBlockedException) {
            return HttpStatus.FORBIDDEN;
        }

        if (exception instanceof NotAuthenticatedException) {
            return HttpStatus.UNAUTHORIZED;
        }

        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    private List<String> convertStackTraceToString(StackTraceElement[] stackTraceElements) {
        return Arrays.stream(stackTraceElements)
                .map(StackTraceElement::toString)
                .collect(Collectors.toList());
    }
}