package org.myorg.application.services.notifications.impl;

import lombok.Getter;
import org.myorg.application.dto.NotificationResponse;
import org.myorg.application.services.notifications.IntegrationLoggerService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class IntegrationLoggerServiceImpl implements IntegrationLoggerService {

    private static final Logger                                        LOGGER    =
            Logger.getLogger(NotificationServiceImpl.class.getName());
    private static       IntegrationLoggerServiceImpl                  instance;
    private final        ScheduledExecutorService                      scheduler =
            Executors.newScheduledThreadPool(1);
    @Getter
    private              List<NotificationResponse>                    responses;
    private volatile     List<CompletableFuture<NotificationResponse>> futures;

    public IntegrationLoggerServiceImpl() {
        this.futures = new ArrayList<>();
        this.responses = new ArrayList<>();
        verifyFutures();
    }

    public static synchronized IntegrationLoggerServiceImpl getInstance() {
        if (instance == null) {
            instance = new IntegrationLoggerServiceImpl();
        }
        return instance;
    }

    public void addFuture(CompletableFuture<NotificationResponse> future) {
        futures.add(future);
    }

    public void verifyFutures() {
        scheduler.scheduleAtFixedRate(() -> {
            for (CompletableFuture<NotificationResponse> future : futures) {
                if (future.isDone()) {
                    try {
                        responses.add(future.get());
                        futures.remove(future);
                    } catch (Exception e) {
                        LOGGER.severe(e.getMessage());
                    }
                }
            }
        }, 20, 16, TimeUnit.SECONDS);
    }
}
