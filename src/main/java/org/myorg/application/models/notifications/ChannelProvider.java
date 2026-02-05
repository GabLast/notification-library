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
public class ChannelProvider extends BaseModel {

    private Channel channel;
    private Provider provider;
    private String apiKey; //generic api key / token for authentication with the provider
                            // some providers use accountSID or other methods...
    private String webhook; //for webhooks implementation
}
