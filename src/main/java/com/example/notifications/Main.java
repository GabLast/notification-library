package com.example.notifications;

import org.myorg.application.models.notifications.Channel;
import org.myorg.application.models.notifications.ChannelProvider;
import org.myorg.application.models.notifications.Message;
import org.myorg.application.models.notifications.Notification;
import org.myorg.application.models.notifications.Provider;
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
import org.myorg.application.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {

    private static final ScheduledExecutorService scheduler    =
            Executors.newScheduledThreadPool(1);
    private static final ScheduledExecutorService logScheduler =
            Executors.newScheduledThreadPool(1);

    public static void main(String[] args) {

        //init "services"
        MessageService messageService = MessageServiceImpl.getInstance();
        ChannelService channelService = ChannelServiceImpl.getInstance();
        ProviderService providerService = ProviderServiceImpl.getInstance();
        NotificationService notificationService = NotificationServiceImpl.getInstance();
        ChannelProviderService channelProviderService =
                ChannelProviderServiceImpl.getInstance();


        //scheduler to show data
        scheduler.scheduleAtFixedRate(() -> {
            List<Notification> list = notificationService.getCompletedNotifications();
            System.out.println("\nSucessfull Notifications: " + list.size());
            list.forEach(it -> System.out.println(it.toString()));
            System.out.println("\nFailed Notifications: " + notificationService.getFailedNotifications().size());

        }, 1, 10, TimeUnit.SECONDS);

        //scheduler to show non 200 api responses
//        logScheduler.scheduleAtFixedRate(() -> {
//
//            //Muestra los errores generados por envío de notificaciones
//            List<NotificationResponse> list =
//                    integrationLoggerService.getNotOKResponses();
//            System.out.println("\nLog de Errores de API: " + list.size() + " errores");
//            list.forEach(it -> System.out.println(it.toString()));
//        }, 1, 10, TimeUnit.SECONDS);

        //Create messages to send
        List<Message> messages = new ArrayList<>();
        messages.add(messageService.createMessage(
                Message.builder().message("Message body #0")
                        .build())); //message with no subject
        messages.add(messageService.createMessage(
                Message.builder().subject("Subject #1").message("Message body #1")
                        .build())); //normal message
        messages.add(messageService.createMessage(
                Message.builder().subject("Subject #2").message("Message body #2")
                        .build())); //normal message
        messages.add(messageService.createMessage(
                Message.builder().message("Message body #3")
                        .build())); //message with no subject

        //Configure channels and providers
        Provider twilio = providerService.findProviderByName(
                Provider.ProviderTypes.TWILIO.getName());
        Provider mailgun = providerService.findProviderByName(
                Provider.ProviderTypes.MAILGUN.getName());
        Provider newProvider = providerService.addProvider(
                Provider.builder().name("New Provider").build());

        Channel sms = channelService.findChannelByName(Channel.ChannelType.SMS.getName());
        Channel email =
                channelService.findChannelByName(Channel.ChannelType.EMAIL.getName());
        Channel push = channelService.findChannelByName(
                Channel.ChannelType.PUSH_NOTIFICATION.getName());
        Channel slack =
                channelService.findChannelByName(Channel.ChannelType.SLACK.getName());

        //Twilio Config
        ChannelProvider twilioMail =
                channelProviderService.configureChannelProvider(email, twilio,
                        CommonUtils.generateApiKey(), null);
        ChannelProvider twilioSMS =
                channelProviderService.configureChannelProvider(sms, twilio,
                        CommonUtils.generateApiKey(), null);
        ChannelProvider twilioPush =
                channelProviderService.configureChannelProvider(push, twilio,
                        CommonUtils.generateApiKey(), null);
        ChannelProvider twilioSlack =
                channelProviderService.configureChannelProvider(slack, twilio,
                        CommonUtils.generateApiKey(),
                        "https://discord.com/api/webhooks/1194087551192014918/d8LOtqn0uEZZKDE8ge7b-7K_yvIcv_IIEYmUsZ2ma1XgJnGax0RPmREbRhqBbdID27aY");

        //Mailgun Config
        ChannelProvider mailgunMail =
                channelProviderService.configureChannelProvider(email, mailgun,
                        CommonUtils.generateApiKey(), null);

        //New Provider Config
        ChannelProvider newProviderMail =
                channelProviderService.configureChannelProvider(email, newProvider,
                        CommonUtils.generateApiKey(), null);

        //sending messages with no subject to channels that do not require subject
        /* Con este ejemplo, se envian 2 message templates: Message body #0 y Message body #3
         * Al usar 3 integraciones, se generan 6 notificaciones
         * 2 por Push, 2 por Slack y 2 por SMS
         * todas son enviadas mediante la integracion con Twilio
         * */
        notificationService.batchSendNotificationsToConfiguredChannelProviders(
                messages.stream().filter(it -> it.getSubject() == null || it.getSubject()
                        .isBlank()).toList(),
                List.of(twilioPush, twilioSlack, twilioSMS));

        //Envio de mensajes con Asunto a canales que requieren que exista
        /* Con este ejemplo, se envian 2 message templates: Message body #1 y Message body #2
         * Al usar 4 integraciones, se generan 8 notificaciones
         * 6 por mail - 2 twilio, 2 mailgun, 2 "new provider" (proveedor nuevo)
         * 2 por sms mediante twilio
         * */
        notificationService.batchSendNotificationsToConfiguredChannelProviders(
                messages.stream().filter(it -> it.getSubject() != null && !it.getSubject()
                        .isBlank()).toList(),
                List.of(twilioMail, twilioSMS, mailgunMail, newProviderMail));

        //Generar notificacion fallida para retry
        //max 3 retries. si falla 3 veces, la notificacion se pierde
        notificationService.sendNotification("System Notification", "+18091231212", null,
                messageService.createMessage(
                        Message.builder().subject("Retry Notification")
                                .message("This notification has to be retried").build()),
                twilioSMS, true, null);

    }
}
