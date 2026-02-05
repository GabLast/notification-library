package org.myorg.application.models.security;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.myorg.application.models.BaseModel;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Getter
@Setter
public class User extends BaseModel {

    private String name;
    private String email;
    private String phoneNumber;

}
