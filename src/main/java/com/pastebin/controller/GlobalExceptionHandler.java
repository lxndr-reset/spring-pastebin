package com.pastebin.controller;

import com.pastebin.exception.NotAuthenticatedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    //TODO подредачить так чтобы на эксепшены которые не в аннотации выбрасывался новый эксепшн чтобы я в логе его видел

    //TODO Добавить логирование через логгер сначала тут, а потом везде

    @ExceptionHandler(NotAuthenticatedException.class)
    public String NotAuthenticated() {
        return "redirect:/login";
    }
    @ExceptionHandler({Exception.class})
    public String error(Exception exception, Model model) {
        model.addAttribute("exception", exception);

        return "error";
    }


}
