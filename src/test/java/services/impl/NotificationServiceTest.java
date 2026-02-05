package services.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.myorg.application.config.AppConfig;
import org.myorg.application.dto.NotificationResponse;
import org.myorg.application.exceptions.InvalidDataFormat;
import org.myorg.application.models.notifications.ChannelProvider;
import org.myorg.application.models.notifications.Message;
import org.myorg.application.services.notifications.ChannelProviderService;
import org.myorg.application.services.notifications.ChannelService;
import org.myorg.application.services.notifications.MessageService;
import org.myorg.application.services.notifications.NotificationService;
import org.myorg.application.services.notifications.ProviderService;
import org.myorg.application.services.notifications.impl.ChannelProviderServiceImpl;
import org.myorg.application.services.notifications.impl.ChannelServiceImpl;
import org.myorg.application.services.notifications.impl.MessageServiceImpl;
import org.myorg.application.services.notifications.impl.NotificationServiceImpl;
import org.myorg.application.services.notifications.impl.ProviderServiceImpl;
import org.myorg.application.services.security.UserService;
import org.myorg.application.utils.CommonUtils;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class NotificationServiceTest {

    private final NotificationService    service                =
            NotificationServiceImpl.getInstance();
    private final MessageService         messageService         =
            MessageServiceImpl.getInstance();
    private final UserService            userService            =
            UserService.getInstance();
    private final ChannelProviderService channelProviderService =
            ChannelProviderServiceImpl.getInstance();
    private final ChannelService         channelService         =
            ChannelServiceImpl.getInstance();
    private final ProviderService        providerService        =
            ProviderServiceImpl.getInstance();

    @Test
    void sendNotification() throws ExecutionException, InterruptedException {
        Assertions.assertThrows(NullPointerException.class,
                () -> service.sendNotification(null, null, null, null, null, false,
                        null));

        Assertions.assertThrows(NullPointerException.class,
                () -> service.sendNotification(userService.getAdministrator().getEmail(),
                        null, null, null, null, false, null));

        Message messageNoSubject =
                messageService.createMessage(Message.builder().message("Body").build());
        Message messageNormal = messageService.createMessage(
                Message.builder().subject("Subject").message("Body").build());

        ChannelProvider smsTwilio = channelProviderService.configureChannelProvider(
                channelService.findChannelByName("SMS"),
                providerService.findProviderByName("Twilio"),
                CommonUtils.generateApiKey(), null);
        ChannelProvider emailTwilio = channelProviderService.configureChannelProvider(
                channelService.findChannelByName("E-mail"),
                providerService.findProviderByName("Twilio"),
                CommonUtils.generateApiKey(), null);

        Assertions.assertThrows(InvalidDataFormat.class,
                () -> service.sendNotification(userService.getAdministrator().getEmail(),
                        AppConfig.clientMails, null, null, emailTwilio, false, null));
        Assertions.assertThrows(NullPointerException.class,
                () -> service.sendNotification(userService.getAdministrator().getEmail(),
                        AppConfig.clientMails, null, messageNormal, null, false, null));

        //email configuration requires a subject
        NotificationResponse badResponse = service
                .sendNotification(userService.getAdministrator().getEmail(),
                        AppConfig.clientMails, null, messageNoSubject, emailTwilio, false,
                        null).get();
        Assertions.assertNotNull(badResponse);
        Assertions.assertNotEquals(200, badResponse.status());

        NotificationResponse goodResponse = service
                .sendNotification(userService.getAdministrator().getEmail(),
                        AppConfig.clientMails, AppConfig.ccMails, messageNormal,
                        emailTwilio, false, null).get();
        Assertions.assertNotNull(goodResponse);
        Assertions.assertEquals(200, goodResponse.status());

        NotificationResponse badResponse2 = service
                .sendNotification(userService.getAdministrator().getPhoneNumber(),
                        AppConfig.clientMails, null, messageNoSubject, smsTwilio, false,
                        null).get();
        Assertions.assertNotNull(badResponse2);
        Assertions.assertNotEquals(200,
                badResponse2.status()); //due to receivers being emails for sms (sms requires phone number(s)

        NotificationResponse goodResponse2 = service
                .sendNotification(userService.getAdministrator().getPhoneNumber(),
                        AppConfig.clientPhoneNumbers, null, messageNoSubject, smsTwilio,
                        false, null).get();
        Assertions.assertNotNull(goodResponse2);
        Assertions.assertEquals(200, goodResponse2.status());

    }

    @Test
    void batchSendNotificationsToConfiguredChannelProviders() {
        Message messageNormal = messageService.createMessage(
                Message.builder().subject("Subject").message("Body").build());

        ChannelProvider emailTwilio = channelProviderService.configureChannelProvider(
                channelService.findChannelByName("E-mail"),
                providerService.findProviderByName("Twilio"),
                CommonUtils.generateApiKey(), null);

        Assertions.assertDoesNotThrow(
                () -> service.batchSendNotificationsToConfiguredChannelProviders(
                        List.of(messageNormal), List.of(emailTwilio)));
    }

    @Test
    void getCompletedNotifications() {
        Assertions.assertNotNull(service.getCompletedNotifications());
    }

    @Test
    void getFailedNotifications() {
        Assertions.assertNotNull(service.getFailedNotifications());
    }
}
