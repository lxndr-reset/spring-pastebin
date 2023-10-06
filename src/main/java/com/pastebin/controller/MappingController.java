package com.pastebin.controller;

import com.pastebin.dao.MessageAccessRepo;
import com.pastebin.entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MappingController {
    private final MessageAccessRepo messageService;

    @Autowired
    public MappingController(MessageAccessRepo messageService) {
        this.messageService = messageService;
    }

    @RequestMapping("/get-message")
    public String getMessage(Model model, @RequestParam String hash) {
        String message = messageService.findById(hash).orElseThrow(() ->
                new IllegalArgumentException("Invalid hash: " + hash)).getMessage();
        model.addAttribute("hash", hash);
        model.addAttribute("message", message);

        return "your_hash";
    }

    @RequestMapping("/new-message")
    public String newMessage(Model model, @RequestParam String content){
        Message message = new Message(content);
        messageService.save(message);
        model.addAttribute("new-message", message);

        return "new_message";
    }

}
