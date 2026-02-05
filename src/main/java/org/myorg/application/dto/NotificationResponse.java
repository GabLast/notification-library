package org.myorg.application.dto;

import lombok.Builder;
import org.myorg.application.models.notifications.ChannelProvider;
import org.myorg.application.models.notifications.Message;

@Builder
public record NotificationResponse(Integer status, String body, Message message, ChannelProvider channelProvider) {
    @Override
    public String toString() {
        return String.format("\nStatus: %d\tResponse: %s", status, body);
    }
}
