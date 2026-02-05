package org.myorg.application.models.notifications;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.myorg.application.models.BaseModel;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Getter
@Setter
public class Provider extends BaseModel {

    private String name;

    @Getter
    @AllArgsConstructor
    public enum ProviderTypes {
        TWILIO( "Twilio"),
        MAILGUN( "Mailgun");

        private final String name;
    }
}
