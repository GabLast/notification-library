package org.myorg.application.models.notifications;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.myorg.application.models.BaseModel;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Getter
@Setter
public class Notification extends BaseModel {

    private Message  message;
    private Provider provider;
    private Channel  channel;

    private String creator;
    private String receiver;
    private String cc;

    private boolean sent;
    private int     retryAttempts = 0;

    @Override
    public String toString() {
        return String.format(
                "*********************Notification:********"
                        + "\nMessage: %s\nProvider: %s\nChannel: %s\nStatus: %s\nRetries: %d\n"
                        + "******************************************",
                message.getMessage(), provider.getName(), channel.getName(),
                sent ? "Sent" : "Failed", retryAttempts);
    }
}
