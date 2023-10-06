package com.pastebin.controller;

import com.pastebin.dao.MessageAccessRepo;
import com.pastebin.entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.NoSuchElementException;
import java.util.Optional;

@Controller
public class MappingController {
    private final MessageAccessRepo messageService;

    @Autowired
    public MappingController(MessageAccessRepo messageService) {
        this.messageService = messageService;
    }

    private Message retrieveMessageById(Long id) {
        Optional<Message> message = messageService.findById(id);
        if (message.isEmpty() || message.get().getDeleted()) {
            throw new NoSuchElementException("Message with id " + id + " not found");
        }
        return message.get();
    }

    @RequestMapping("/get-message")
    public String getMessage(Model model, @RequestParam Long id) {
        Message message = retrieveMessageById(id);
        model.addAttribute("message", message);

        return "get_message";
    }

    @RequestMapping("/new-message")
    public String newMessage(Model model, @RequestParam String content) {
        Message message = new Message(content);
        messageService.save(message);
        model.addAttribute("message", message);

        return "get_message";
    }

    @ExceptionHandler(value = Exception.class)
    public String error(Exception exception, Model model) {
        model.addAttribute("exception", exception);

        return "error";
    }

    @RequestMapping("/edit-message")
    public String editMessage(Model model, @RequestParam String newContent, @RequestParam Long id) {
        Message message = retrieveMessageById(id);
        message.setValue(newContent);
        messageService.save(message);

        model.addAttribute(message);
        return "get_message";
    }

    @RequestMapping("delete-message")
    public String deleteMessage(Model model, @RequestParam Long id) {
        Message message = retrieveMessageById(id);
        messageService.delete(message);
        model.addAttribute("message", message);

        return "get_message";
    }

}
