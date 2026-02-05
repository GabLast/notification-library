package org.myorg.application.services.notifications;

import org.myorg.application.dto.NotificationResponse;
import org.myorg.application.models.notifications.ChannelProvider;
import org.myorg.application.models.notifications.Message;
import org.myorg.application.models.notifications.Notification;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface NotificationService {

    public CompletableFuture<NotificationResponse> sendNotification(String messageCreator,
            String receiver, String cc, Message message, ChannelProvider channelProvider,
            boolean forceError, Notification notificationRetry);

    public void batchSendNotificationsToConfiguredChannelProviders(List<Message> messages,
            List<ChannelProvider> configuredProviders);

    public List<Notification> getPendingNotificationsByMaxRetries(int retries);
    public List<Notification> getCompletedNotifications();
    public List<Notification> getFailedNotifications();
}
