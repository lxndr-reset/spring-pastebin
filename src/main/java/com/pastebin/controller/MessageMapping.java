package com.pastebin.controller;

import com.pastebin.date.ValidTime;
import com.pastebin.entity.Message;
import com.pastebin.entity.User;
import com.pastebin.service.entity_service.MessageService;
import com.pastebin.service.entity_service.ShortURLService;
import com.pastebin.service.user_details.UserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.naming.AuthenticationException;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping(value = "/message")
public class MessageMapping {
    private final MessageService messageService;
    private final ShortURLService shortURLService;
    private final Logger logger = LoggerFactory.getLogger(MessageMapping.class);

    @Autowired
    public MessageMapping(MessageService messageService, ShortURLService shortURLService) {
        this.messageService = messageService;
        this.shortURLService = shortURLService;
    }

    /**
     * Converts a string representation of a deletion date to a ValidTime object.
     *
     * @param stringDeletionDate a string representing the deletion date
     * @return the ValidTime object corresponding to the deletion date
     * @throws IllegalArgumentException if the stringDeletionDate is invalid or not supported
     */
    private static ValidTime getValidTimeDate(String stringDeletionDate) {

        try {
            return ValidTime.valueOf(stringDeletionDate);

        } catch (IllegalArgumentException e) {

            throw new IllegalArgumentException("Wrong or missing option " + stringDeletionDate +
                    ".\n Available options: " + "ONE_HOUR, ONE_DAY, ONE_WEEK, TWO_WEEKS, ONE_MONTH, THREE_MONTHS"
            );
        }
    }

    /**
     * Retrieves a message by its value from the message service and sets it as an attribute in a ModelAndView object.
     *
     * @param mav   the ModelAndView object to which the retrieved message will be added
     * @param value the value of the message to be retrieved
     * @return the ModelAndView object with the retrieved message set as an attribute
     * @throws NoSuchElementException if a message with the given value does not exist
     */
    @GetMapping("/get/{value}")
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

    /**
     * Retrieves all messages for the authenticated user, if available, and sets the user as an attribute in the Model object.
     *
     * @param modelAndView the ModelAndView object to which the user will be added
     * @return the name of the view to be rendered for displaying all messages
     * @throws AuthenticationException if the user is not authenticated
     */
    @GetMapping("/get/all")
    public ModelAndView getAllUsersMessages(ModelAndView modelAndView) throws AuthenticationException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails details = (UserDetails) authentication.getPrincipal();

        String email = details.getUsername();

        if (!email.equalsIgnoreCase("anonymousUser")) {
            modelAndView.getModelMap().addAttribute("user", createTransferUserByEmail(email));
            modelAndView.setViewName("get_all_messages");
            return modelAndView;
        }

        throw new AuthenticationException("You are not authorized");
    }

    private User createTransferUserByEmail(String email) {

        User transferUser = new User();
        transferUser.setEmail(email);
        transferUser.setAllUsersMessages(
                messageService.getMessagesByUser_Email(email)
        );

        return transferUser;
    }


    @GetMapping("/new/{content}/{stringDeletionDate}")
    public ModelAndView newMessage(ModelAndView modelAndView, @PathVariable String content,
                                   @PathVariable String stringDeletionDate) {

        Message message = new Message(content,
                shortURLService.getAvailableShortURL(),
                getValidTimeDate(stringDeletionDate)
        );

        return persistMessageAndGetMAV(message, modelAndView);
    }


    /**
     * Renders a form for creating a new message and sets an empty Message object as a model attribute.
     *
     * @param modelAndView the ModelAndView object to be used for rendering the view
     * @return the ModelAndView object representing the view for creating a new message
     */
    @GetMapping(value = "/new")
    public ModelAndView newMessage(ModelAndView modelAndView) {

        modelAndView.setViewName("new_message");
        modelAndView.getModelMap().addAttribute("message", new Message());

        return modelAndView;
    }

    /**
     * Submits a new message by setting the deletion date, generating a short URL, and persisting the message.
     * The message object is passed as a model attribute and the ModelAndView object is returned.
     *
     * @param message      the Message object containing the message details
     * @param modelAndView the ModelAndView object to be returned
     * @return the ModelAndView object containing the persisted message
     */
    @PostMapping(value = "/submit")
    public ModelAndView submitMessage(@ModelAttribute("message") Message message,
                                      ModelAndView modelAndView) {

        if (message != null && message.getDeletionDateText() != null) {
            message.setDeletionDate(message.getDeletionDateText());
            message.setShortURL(shortURLService.getAvailableShortURL());
        }

        return persistMessageAndGetMAV(message, modelAndView);
    }

    /**
     * Persists the given message and returns the updated ModelAndView object.
     *
     * @param message      the Message object to be persisted
     * @param modelAndView the ModelAndView object to be updated
     * @return the ModelAndView object after persisting the message
     */
    private ModelAndView persistMessageAndGetMAV(Message message, ModelAndView modelAndView) {

        messageService.save(message);

        modelAndView.setViewName(
                "redirect:/message/get/" + message.getShortURL().getUrlValue()
        );

        return modelAndView;
    }

    @GetMapping("/edit/{value}/{content}")
    public ModelAndView editMessageContent(ModelAndView modelAndView, @PathVariable String value, @PathVariable String content)
            throws ExecutionException, InterruptedException {

        Message message = messageService.findByShortURLValue(value).get();

        message.setValue(content);

        messageService.save(message);

        modelAndView.getModelMap().addAttribute("message", message);
        modelAndView.setViewName("get_message");

        return modelAndView;
    }

    /**
     * Edits the content and deletion date of a message and returns the corresponding view name.
     *
     * @param modelAndView the ModelAndVie object to add attributes and view to
     * @param value        the value of the short URL associated with the message
     * @param content      the new content of the message
     * @param deletionDate the new deletion date of the message
     * @return the view name for displaying the updated message
     * @throws ExecutionException   if an error occurs during execution
     * @throws InterruptedException if the thread is interrupted while waiting for the message retrieval
     */
    @GetMapping("/edit/{value}/{content}/{deletionDate}")
    public ModelAndView editMessageContentAndDeletionDate(ModelAndView modelAndView, @PathVariable String value,
                                                          @PathVariable String content, @PathVariable String deletionDate)
            throws ExecutionException, InterruptedException {

        Message message = messageService.findByShortURLValue(value).get();

        message.setValue(content);
        message.setDeletionDate(getValidTimeDate(deletionDate));

        messageService.save(message);

        modelAndView.getModelMap().addAttribute("message", message);
        modelAndView.setViewName("get_message");

        return modelAndView;
    }


    @GetMapping("/edit-time/{value}/{deletionDate}")
    public ModelAndView editMessageDeletionDate(ModelAndView modelAndView, @PathVariable String value,
                                                @PathVariable String deletionDate)
            throws ExecutionException, InterruptedException {

        Message message = messageService.findByShortURLValue(value).get();

        message.setDeletionDate(
                getValidTimeDate(deletionDate)
        );

        messageService.save(message);

        modelAndView.getModelMap().addAttribute("message", message);
        modelAndView.setViewName("get_message");

        return modelAndView;
    }


    @GetMapping("/delete/{value}")
    public ModelAndView deleteMessage(ModelAndView modelAndView, @PathVariable String value)
            throws ExecutionException, InterruptedException {

        Message message = messageService.softDeleteByValue(value).get();

        modelAndView.getModelMap().addAttribute("message", message);
        modelAndView.setViewName("get_message");

        return modelAndView;
    }

}
