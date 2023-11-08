package com.pastebin.controller;

import com.pastebin.date.ValidTime;
import com.pastebin.entity.Message;
import com.pastebin.entity.User;
import com.pastebin.service.entityService.MessageService;
import com.pastebin.service.entityService.ShortURLService;
import com.pastebin.service.user_details.UserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.naming.AuthenticationException;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;

@Controller
@RequestMapping("/message")
public class MessageMapping {
    private final MessageService messageService;
    private final ShortURLService shortURLService;
    private final Logger logger = LoggerFactory.getLogger(MessageMapping.class);

    @Autowired
    public MessageMapping(MessageService messageService, ShortURLService shortURLService) {
        this.messageService = messageService;
        this.shortURLService = shortURLService;
    }

    private static ValidTime getValidTimeDate(String stringDeletionDate) {
        try {
            return ValidTime.valueOf(stringDeletionDate);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Wrong or missing option " + stringDeletionDate + ".\n Available options: " + "ONE_HOUR, ONE_DAY, ONE_WEEK, TWO_WEEKS, ONE_MONTH, THREE_MONTHS");
        }
    }

    @RequestMapping("/get/{value}")
    public ModelAndView getMessageByValue(ModelAndView mav, @PathVariable String value) {
        if (mav.getModelMap().containsAttribute("message")) {
            return mav;
        }

        Message message;
        try {
            message = messageService.findByShortURLValue(value).join();
        } catch (NoSuchElementException e) {
            logger.debug("Element by value '" + value + "' not found");
            throw e;
        }
        mav.getModelMap().addAttribute("message", message);
        mav.setViewName("get_message");

        return mav;
    }

    @RequestMapping("/get/all")
    public String getAllUsersMessages(Model model) throws AuthenticationException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserDetails details = (UserDetails) authentication.getPrincipal();
        String email = details.getUsername();

        if (!email.equalsIgnoreCase("anonymousUser")) {
            model.addAttribute("user", createTransferUserByEmail(email)); //todo refactor all model attributes from user to userDTO
            return "get_all_messages";
        }
        throw new AuthenticationException("You are not authorized");
    }

    private User createTransferUserByEmail(String email) {
        User transferUser = new User();
        transferUser.setEmail(email);
        transferUser.setAllUsersMessages(messageService.getMessagesByUser_Email(email));

        return transferUser;
    }


    @RequestMapping("/new/{content}/{stringDeletionDate}")
    public ModelAndView newMessage(ModelAndView modelAndView, @PathVariable String content, @PathVariable String stringDeletionDate) {
        Message message = new Message(content,
                shortURLService.getAvailableShortURL(),
                getValidTimeDate(stringDeletionDate)
        );

        return persistMessageAndGetMAV(message, modelAndView);
    }


    @RequestMapping(value = "/new")
    public ModelAndView newMessage(ModelAndView modelAndView) {
        modelAndView.setViewName("new_message");
        modelAndView.getModelMap().addAttribute("message", new Message());

        return modelAndView;
    }

    @RequestMapping(value = "/submit", method = RequestMethod.POST)
    public ModelAndView submitMessage(@ModelAttribute("message") Message message,
                                      ModelAndView modelAndView) {
        assert message != null;

        if (message.getDeletionDateText() != null){
            message.setDeletionDate(message.getDeletionDateText());
        }

        message.setShortURL(shortURLService.getAvailableShortURL());

        return persistMessageAndGetMAV(message, modelAndView);
    }
    private ModelAndView persistMessageAndGetMAV(Message message, ModelAndView modelAndView) {
        messageService.save(message);

        modelAndView.setViewName(
                "redirect:/message/get/" + message.getShortURL().getUrlValue()
        );

        return modelAndView;
    }

    @RequestMapping("/edit/{value}/{content}")
    public String editMessageContent(Model model, @PathVariable String value, @PathVariable String content)
            throws ExecutionException, InterruptedException {

        Message message = messageService.findByShortURLValue(value).get();
        message.setValue(content);
        messageService.save(message);

        model.addAttribute("message", message);

        return "get_message";
    }

    @RequestMapping("/edit/{value}/{content}/{deletionDate}")
    public String editMessageContentAndDeletionDate(Model model, @PathVariable String value, @PathVariable String content, @PathVariable String deletionDate) throws ExecutionException, InterruptedException {

        Message message = messageService.findByShortURLValue(value).get();
        message.setValue(content);
        message.setDeletionDate(getValidTimeDate(deletionDate));

        messageService.save(message);

        model.addAttribute("message", message);

        return "get_message";
    }

    @RequestMapping("/edit-time/{value}/{deletionDate}")
    public String editMessageDeletionDate(Model model, @PathVariable String value, @PathVariable String deletionDate) throws ExecutionException, InterruptedException {

        Message message = messageService.findByShortURLValue(value).get();
        message.setDeletionDate(getValidTimeDate(deletionDate));
        messageService.save(message);

        model.addAttribute("message", message);

        return "get_message";
    }


    @RequestMapping("/delete/{value}")
    public String deleteMessage(Model model, @PathVariable String value) throws ExecutionException, InterruptedException {
        Message message = messageService.softDeleteByValue(value).get();
        model.addAttribute("message", message);

        return "get_message";
    }

}
