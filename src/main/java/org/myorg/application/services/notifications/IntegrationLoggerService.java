package org.myorg.application.services.notifications;

import org.myorg.application.dto.NotificationResponse;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface IntegrationLoggerService {

    public List<NotificationResponse> getResponses();
    public void addFuture(CompletableFuture<NotificationResponse> future);
    public void verifyFutures();
}
