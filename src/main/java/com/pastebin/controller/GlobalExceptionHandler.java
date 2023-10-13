package com.pastebin.controller;

import com.pastebin.exception.*;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExceptionHandler {
//    TODO подредачить так чтобы на эксепшены которые не в аннотации выбрасывался новый эксепшн чтобы я в логе его видел
//    TODO Добавить логирование через логгер сначала тут, а потом везде

    @ExceptionHandler(NotAuthenticatedException.class)
    public String NotAuthenticated() {
        return "redirect:/login";
    }

    @ExceptionHandler(value = {NoSuchElementException.class, MessageDeletedException.class,
            NoAvailableShortURLException.class, UrlNotExistsException.class, UserBlockedException.class,
            IllegalArgumentException.class
    })
    public String handleError(Model model, Exception exception) {
        model.addAttribute("message", exception.getMessage());
        model.addAttribute("exceptionName", exception.getClass().getName());
        model.addAttribute("currentTime", LocalDateTime.now());
        model.addAttribute("stack_trace", exception.getStackTrace());

        return "error";
    }

}
