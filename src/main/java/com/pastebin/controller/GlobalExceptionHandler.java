package com.pastebin.controller;

import com.pastebin.exception.NotAuthenticatedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;

@ControllerAdvice
@Controller
public class GlobalExceptionHandler {
    //TODO подредачить так чтобы на эксепшены которые не в аннотации выбрасывался новый эксепшн чтобы я в логе его видел
    //TODO Добавить логирование через логгер сначала тут, а потом везде

    @ExceptionHandler(NotAuthenticatedException.class)
    public String NotAuthenticated() {
        return "redirect:/login";
    }

    @ExceptionHandler({Exception.class})
    public String handleError(Model model, Exception exception) {
        model.addAttribute("exception", exception);
        model.addAttribute("message", exception.getMessage());
        model.addAttribute("exceptionName", exception.getClass().getName());
        model.addAttribute("currentTime", LocalDateTime.now());

        return "error";
    }

}
