package com.pastebin.controller;

import com.pastebin.auth.AuthenticationStatus;
import com.pastebin.entity.Message;
import com.pastebin.entity.ShortURL;
import com.pastebin.entity.User;
import com.pastebin.entity.date.ValidTime;
import com.pastebin.exception.NoAvailableShortURLException;
import com.pastebin.service.MessageService;
import com.pastebin.service.ShortURLService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/message")
public class MessageMapping {
    private final MessageService messageService;
    private final ShortURLService shortURLService;
    private final AuthenticationStatus authenticationStatus;
    private final Logger logger = LoggerFactory.getLogger(MessageMapping.class);

    @Autowired
    public MessageMapping(MessageService messageService, ShortURLService shortURLService, AuthenticationStatus authenticationStatus) {
        this.messageService = messageService;
        this.shortURLService = shortURLService;
        this.authenticationStatus = authenticationStatus;
    }

    private static ValidTime getValidTimeDate(String stringDeletionDate) {
        try {
            return ValidTime.valueOf(stringDeletionDate);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Missing option " + stringDeletionDate + ".\n Available options: " +
                    "ONE_HOUR, ONE_DAY, ONE_WEEK, TWO_WEEKS, ONE_MONTH, THREE_MONTHS");
        }
    }

    @RequestMapping("/get/{value}")
    public String getMessageByValue(Model model, @PathVariable String value) {
        Message message = messageService.findByValue(value);
        model.addAttribute("message", message);

        return "get_message";
    }

    @RequestMapping("/get/all")
    public String getAllUsersMessages(Model model, @ModelAttribute("user") User user, @ModelAttribute("email") String enteredEmail) {
        User authenticatedUser = authenticationStatus.getAuthenticatedUser();
        if (authenticatedUser.getEmail().isEmpty()) {
            return "redirect:/";
        }
        model.addAttribute("user", authenticatedUser);

        return "welcome";

    }

    @RequestMapping("/new/{content}/{stringDeletionDate}")
    public String newMessage(Model model, @PathVariable String content,
                             @PathVariable String stringDeletionDate) throws NoAvailableShortURLException {
        ShortURL shortURL = shortURLService.getAvailableShortURL();
        Message message;

        message = new Message(content, shortURL, getValidTimeDate(stringDeletionDate));
        shortURL.setMessage(message);
        messageService.save(message);
        model.addAttribute("message", message);

        return "get_message";
    }

    @RequestMapping("/edit/{value}/{content}")
    public String editMessageContent(Model model, @PathVariable String value, @PathVariable String content) {
        Message message = messageService.findByValue(value);
        message.setValue(content);
        messageService.save(message);

        model.addAttribute("message", message);
        return "get_message";
    }

    @RequestMapping("/edit/{value}/{content}/{deletionDate}")
    public String editMessageContentAndDeletionDate(Model model, @PathVariable String value,
                                                    @PathVariable String content, @PathVariable String deletionDate) {
        Message message = messageService.findByValue(value);
        message.setValue(content);
        message.setDeletionDate(getValidTimeDate(deletionDate));
        messageService.save(message);

        model.addAttribute("message", message);
        return "get_message";
    }

    @RequestMapping("/edit-time/{value}/{deletionDate}")
    public String editMessageDeletionDate(Model model, @PathVariable String value,
                                          @PathVariable String deletionDate) {
        Message message = messageService.findByValue(value);
        message.setDeletionDate(getValidTimeDate(deletionDate));
        messageService.save(message);

        model.addAttribute("message", message);
        return "get_message";
    }


    @RequestMapping("/delete/{value}")
    public String deleteMessage(Model model, @PathVariable String value) {
        Message message = messageService.softDeleteByValue(value);
        model.addAttribute("message", message);

        return "get_message";
    }

}
