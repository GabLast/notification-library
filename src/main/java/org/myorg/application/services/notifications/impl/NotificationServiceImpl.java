package org.myorg.application.services.notifications.impl;

import org.myorg.application.config.AppConfig;
import org.myorg.application.dto.NotificationResponse;
import org.myorg.application.exceptions.InvalidDataFormat;
import org.myorg.application.models.notifications.Channel;
import org.myorg.application.models.notifications.ChannelProvider;
import org.myorg.application.models.notifications.Message;
import org.myorg.application.models.notifications.Notification;
import org.myorg.application.services.notifications.ChannelProviderService;
import org.myorg.application.services.notifications.IntegrationLoggerService;
import org.myorg.application.services.notifications.NotificationService;
import org.myorg.application.services.security.UserService;
import org.myorg.application.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class NotificationServiceImpl implements NotificationService {

    private static final Logger                   LOGGER    =
            Logger.getLogger(NotificationServiceImpl.class.getName());
    private static       NotificationServiceImpl  instance;
    private final        ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(1);

    private UserService              userService;
    private ChannelProviderService   channelProviderService;
    private IntegrationLoggerService integrationLoggerService;

    private List<Notification> notifications;
    private Long               id = 1L;

    public NotificationServiceImpl() {
        this.notifications = new ArrayList<>();
        boostrap();
    }

    public static synchronized NotificationServiceImpl getInstance() {
        if (instance == null) {
            instance = new NotificationServiceImpl();
        }
        return instance;
    }

    private void boostrap() {
        // "inject" services
        channelProviderService = ChannelProviderServiceImpl.getInstance();
        integrationLoggerService = IntegrationLoggerServiceImpl.getInstance();
        userService = UserService.getInstance();

        //init retry scheduler
        retryPendingNotifications();
    }

    //sends notification to all the clients through a single channel
    public CompletableFuture<NotificationResponse> sendNotification(String messageCreator,
            String receiver, String cc, Message message, ChannelProvider channelProvider,
            boolean forceError, Notification notificationRetry) {

        if (messageCreator == null || messageCreator.isBlank()) {
            throw new NullPointerException("Message creator can not be null");
        }

        if (receiver == null || receiver.isBlank()) {
            throw new NullPointerException("Message receiver can not be null");
        }

        if (channelProvider == null || channelProvider.getChannel() == null
                || channelProvider.getProvider() == null) {
            throw new NullPointerException(
                    "Channel provider is null. Please verify the configured channel for the selected provider.");
        }

        if (message == null || message.getMessage() == null || message.getMessage()
                .isBlank()) {
            throw new InvalidDataFormat("Message template has no message to send");
        }

        CompletableFuture<NotificationResponse> notificationFuture =
                CompletableFuture.supplyAsync(() -> {

                    Notification notification = Notification.builder().message(message)
                            .channel(channelProvider.getChannel())
                            .provider(channelProvider.getProvider())
                            .creator(messageCreator).receiver(receiver).cc(cc).build();

                    try {

                        //validate message creator
                        if (channelProvider.getChannel().getName()
                                .equalsIgnoreCase(Channel.ChannelType.EMAIL.getName())) {

                            if (!CommonUtils.isValidEmail(messageCreator)) {
                                throw new NullPointerException(
                                        "Message creator e-mail is invalid");
                            }

                            //validate receiver mails
                            for (String mail : receiver.split(",")) {
                                if (!CommonUtils.isValidEmail(mail)) {
                                    throw new NullPointerException(
                                            "Receiver Mail is Invalid: " + mail);
                                }
                            }

                            //validate cc mails
                            if (cc != null && !cc.isBlank()) {
                                for (String carbonCopyMail : cc.split(",")) {
                                    if (!CommonUtils.isValidEmail(carbonCopyMail)) {
                                        throw new NullPointerException(
                                                "CC Mail is Invalid: " + carbonCopyMail);
                                    }
                                }
                            }

                            if (message.getSubject() == null || message.getSubject()
                                    .isBlank()) {
                                throw new InvalidDataFormat(
                                        "The message subject can not be null for e-mails");
                            }
                        }

                        //validate message receiver
                        if (channelProvider.getChannel().getName()
                                .equalsIgnoreCase(Channel.ChannelType.SMS.getName())) {

                            for (String number : receiver.split(",")) {
                                if (!CommonUtils.isValidPhoneNumber(number)) {
                                    throw new InvalidDataFormat(
                                            "Invalid Phone Number: " + number);
                                }
                            }
                        }

                        //mimic an api and final configurations (post on webhook or so)
                        if (channelProvider.getChannel().getName()
                                .equalsIgnoreCase(Channel.ChannelType.EMAIL.getName())) {
                            try {
                                // sleep to simulate an api request
                                // api request to provider
                                // https://documentation.mailgun.com/docs/mailgun/api-reference/send/mailgun/messages/get-v3-domains--domain-name--messages--storage-key-
                                Thread.sleep(3 * 1000);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        } else if (channelProvider.getChannel().getName()
                                .equalsIgnoreCase(Channel.ChannelType.SMS.getName())) {
                            try {
                                // sleep to simulate an api request
                                Thread.sleep(2 * 1000);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        } else if (channelProvider.getChannel().getName()
                                .equalsIgnoreCase(
                                        Channel.ChannelType.PUSH_NOTIFICATION.getName())) {
                            try {
                                // sleep to simulate an api request
                                Thread.sleep(1 * 1000);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        } else if (channelProvider.getChannel().getName()
                                .equalsIgnoreCase(Channel.ChannelType.SLACK.getName())) {
                            try {
                                // sleep to simulate an api request
                                //Post to a webhook with contentType Application/JSON
                                //https://www.twilio.com/docs/studio/tutorials/how-to-post-sms-to-slack
                                Thread.sleep(5 * 1000);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        } else {
                            throw new Exception("This channel is not supported");
                        }

                        if (forceError) {
                            throw new Exception("Forced Error");
                        }

                        notification.setSent(true);

                        if (notificationRetry == null) {
                            //to block different
                            //threads from trying a WRITE
                            //at the same time
                            //****mostly caused due to not using a framework/jobs/message queues...
                            synchronized (this) {
                                notifications.add(notification);
                            }
                        } else {
                            notificationRetry.setSent(true);
                        }

                        //                            LOGGER.info(String.format(
                        //                                    "Notification sent.  Message [%s]\tProvider [%s]\tChannel [%s]",
                        //                                    message.getMessage(),
                        //                                    channelProvider.getProvider().getName(),
                        //                                    channelProvider.getChannel().getName()));

                        return NotificationResponse.builder().status(200).message(message)
                                .channelProvider(channelProvider).body("Sucess").build();

                    } catch (Exception e) {

                        if (notificationRetry == null) {

                            //to block different
                            //threads from trying a WRITE
                            //at the same time
                            //****mostly caused due to not using a framework/jobs/message queues...
                            synchronized (this) {
                                notifications.add(notification);
                            }
                        }

                        NotificationResponse error =
                                NotificationResponse.builder().status(400)
                                        .body("Error: " + String.format(
                                                "%s failed to send a %s notification: %s",
                                                channelProvider.getProvider().getName(),
                                                channelProvider.getChannel().getName(),
                                                e.getMessage())).message(message)
                                        .channelProvider(channelProvider).build();
                        return error;
                    }
                });

        //            LOGGER.info(
        //                    String.format("Trying to send message: [%s]", message.getMessage()));

        return notificationFuture;

    }

    public void batchSendNotificationsToConfiguredChannelProviders(List<Message> messages,
            List<ChannelProvider> configuredProviders) {

        if (configuredProviders == null || configuredProviders.isEmpty()) {
            configuredProviders = channelProviderService.getConfiguredChannelProviders();
        }

        for (ChannelProvider channelProvider : configuredProviders) {
            for (Message message : messages) {
                if (channelProvider.getChannel().getName()
                        .equalsIgnoreCase(Channel.ChannelType.EMAIL.getName())) {

                    try {
                        sendNotification(
                                userService.getAdministrator().getEmail(),
                                AppConfig.clientMails, AppConfig.ccMails, message,
                                channelProvider, false, null);
                    } catch (Exception e) {
                        LOGGER.info(
                                "Caught Exception on batchSendNotificationsToConfiguredChannelProviders: "
                                        + e.getMessage());
                    }

                } else if (channelProvider.getChannel().getName()
                        .equalsIgnoreCase(Channel.ChannelType.SLACK.getName())) {
                    try {
                        sendNotification("Business SLACK AllNotif",
                                AppConfig.clientMails, null, message,
                                channelProvider, false, null);
                    } catch (Exception e) {
                        LOGGER.info(
                                "Caught Exception on batchSendNotificationsToConfiguredChannelProviders: "
                                        + e.getMessage());
                    }

                } else {
                    try {
                        sendNotification("Business AllNotif",
                                AppConfig.clientPhoneNumbers, null, message,
                                channelProvider, false, null);
                    } catch (Exception e) {
                        LOGGER.info(
                                "Caught Exception on batchSendNotificationsToConfiguredChannelProviders: "
                                        + e.getMessage());
                    }
                }
            }
        }
    }

    public List<Notification> getPendingNotificationsByMaxRetries(int retries) {
        return notifications.stream()
                .filter(it -> !it.isSent() && it.getRetryAttempts() < retries).toList();
    }

    public List<Notification> getCompletedNotifications() {
        return notifications.stream().filter(Notification::isSent).toList();
    }

    public List<Notification> getFailedNotifications() {
        return notifications.stream()
                .filter(it -> !it.isSent() && it.getRetryAttempts() >= 3).toList();
    }

    private void retryPendingNotifications() {

        scheduler.scheduleAtFixedRate(() -> {

            List<Notification> list = getPendingNotificationsByMaxRetries(3);
            LOGGER.info("Retrying Notifications - Current Amount: " + list.size());
            for (Notification notification : list) {
                notification.setRetryAttempts(notification.getRetryAttempts() + 1);
                retryNotification(notification);
            }
        }, 15, 15, TimeUnit.SECONDS);
    }

    public void retryNotification(Notification notification) {
        ChannelProvider channelProvider =
                ChannelProvider.builder().channel(notification.getChannel())
                        .provider(notification.getProvider()).build();

        //adding random chance to fail
        boolean isFail = CommonUtils.generateBoolean50PercentTrue();

        sendNotification(notification.getCreator(), notification.getReceiver(),
                notification.getCc(), notification.getMessage(), channelProvider,
                isFail, notification);
    }

}
