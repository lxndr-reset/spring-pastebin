package com.pastebin.controller;

import com.pastebin.entity.Message;
import com.pastebin.entity.ShortURL;
import com.pastebin.entity.date.ValidTime;
import com.pastebin.exception.MessageDeletedException;
import com.pastebin.exception.NoAvailableShortURLException;
import com.pastebin.exception.UrlNotExistsException;
import com.pastebin.exception.UserBlockedException;
import com.pastebin.service.MessageService;
import com.pastebin.service.ShortURLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.NoSuchElementException;

@Controller
public class MappingController {
    private final MessageService messageService;
    private final ShortURLService shortURLService;

    @Autowired
    public MappingController(MessageService messageService, ShortURLService shortURLService) {
        this.messageService = messageService;
        this.shortURLService = shortURLService;
    }


    @RequestMapping("/get-message")
    public String getMessageByValue(Model model, @RequestParam Long id) {
        Message message = messageService.findById(id);
        model.addAttribute("message", message);

        return "get_message";
    }

    @RequestMapping("/get-message/{value}")
    public String getMessageByValue(Model model, @PathVariable String value) {
        Message message = messageService.findByValue(value);
        model.addAttribute("message", message);

        return "get_message";
    }

    @RequestMapping("/new-message")
    public String newMessage(Model model, @RequestParam String content) throws NoAvailableShortURLException {
        ShortURL shortURL = shortURLService.getAvailableShortURL();
        Message message = new Message(content, shortURL);
        shortURL.setMessage(message);

        messageService.save(message);
        model.addAttribute("message", message);

        return "get_message";
    }

    @RequestMapping("/new-message/{content}/{stringDeletionDate}")
    public String newMessage(Model model, @PathVariable String content, @PathVariable String stringDeletionDate) throws NoAvailableShortURLException {
        ShortURL shortURL = shortURLService.getAvailableShortURL();
        Message message;

        try {
            ValidTime deletionDate = ValidTime.valueOf(stringDeletionDate);
            message = new Message(content, shortURL, deletionDate);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Missing option " + stringDeletionDate + ".\n Available options: " +
                    "ONE_HOUR, ONE_DAY, ONE_WEEK, TWO_WEEKS, ONE_MONTH, THREE_MONTHS");
        }

        shortURL.setMessage(message);
        messageService.save(message);
        model.addAttribute("message", message);

        return "get_message";
    }

    @ExceptionHandler(value = {NoSuchElementException.class, MessageDeletedException.class,
            NoAvailableShortURLException.class, UrlNotExistsException.class, UserBlockedException.class,
            IllegalArgumentException.class
    })
    public String error(Exception exception, Model model) {
        model.addAttribute("exception", exception);

        return "error";
    }

    @RequestMapping("/edit-message")
    public String editMessage(Model model, @RequestParam String content, @RequestParam Long id) {
        Message message = messageService.findById(id);
        message.setValue(content);
        messageService.save(message);

        model.addAttribute(message);
        return "get_message";
    }

    @RequestMapping("/delete-message")
    public String deleteMessage(Model model, @RequestParam Long id) {
        Message message = messageService.findById(id);
        message.setDeleted(true);
        messageService.save(message);
        model.addAttribute("message", message);

        return "get_message";
    }

    @RequestMapping("/delete-message/{value}")
    public String deleteMessage(Model model, @PathVariable String value) {
        Message message = messageService.softDeleteByValue(value);
        model.addAttribute("message", message);

        return "get_message";
    }
}
