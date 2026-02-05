package org.myorg.application.services.notifications.impl;

import org.myorg.application.exceptions.ResourceNotFoundException;
import org.myorg.application.models.notifications.Message;
import org.myorg.application.services.notifications.MessageService;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class MessageServiceImpl implements MessageService {

    private static final Logger             LOGGER =
            Logger.getLogger(MessageServiceImpl.class.getName());
    private static       MessageServiceImpl instance;

    private MessageServiceImpl() {
        this.messages = new ArrayList<>();
    }

    //synchronized to ensure thread safety
    public static synchronized MessageServiceImpl getInstance() {
        if (instance == null) {
            instance = new MessageServiceImpl();
        }
        return instance;
    }

    private List<Message> messages;
    private Long          id = 1L;

    public Message createMessage(Message message) {

        if (message == null) {
            throw new ResourceNotFoundException("Message can not be null");
        }

        if (message.getMessage() == null || message.getMessage().isBlank()) {
            throw new ResourceNotFoundException("Message name is empty");
        }

        message.setId(id);
        if (message.getContentType() == null || message.getContentType().isBlank()) {
            message.setContentType("text/plain");
        }
        messages.add(message);
        id++;

        return message;

    }
}
