package services.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.myorg.application.exceptions.ResourceNotFoundException;
import org.myorg.application.models.notifications.Message;
import org.myorg.application.services.notifications.MessageService;
import org.myorg.application.services.notifications.impl.MessageServiceImpl;

public class MessageServiceTest {

    private final MessageService service = MessageServiceImpl.getInstance();

    @Test
    void createMessage() {
        Assertions.assertNotNull(
                service.createMessage(Message.builder().message("Message Body").build()));

        Assertions.assertThrows(ResourceNotFoundException.class, () ->
                service.createMessage(Message.builder().build()));
        Assertions.assertThrows(ResourceNotFoundException.class, () ->
                service.createMessage(null));
    }
}
