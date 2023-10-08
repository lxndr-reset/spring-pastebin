package com.pastebin.controller;

import com.pastebin.entity.Message;
import com.pastebin.entity.ShortURL;
import com.pastebin.exception.NoAvailableShortURLException;
import com.pastebin.service.MessageService;
import com.pastebin.service.ShortURLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    public String getMessage(Model model, @RequestParam Long id) {
        Message message = messageService.findById(id);
        model.addAttribute("message", message);

        return "get_message";
    }

    @RequestMapping("/new-message")
    public String newMessage(Model model, @RequestParam String content) throws NoAvailableShortURLException {
        ShortURL shortURL = shortURLService.getByMessageIsNullOrMessageDeleted();
        Message message = new Message(content, shortURL);
        shortURL.setMessage(message);

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
        Message message = messageService.findById(id);
        message.setValue(newContent);
        messageService.save(message);

        model.addAttribute(message);
        return "get_message";
    }

    @RequestMapping("delete-message")
    public String deleteMessage(Model model, @RequestParam Long id) {
        Message message = messageService.findById(id);
        message.setDeleted(true);
        messageService.save(message);
        model.addAttribute("message", message);

        return "get_message";
    }

}
