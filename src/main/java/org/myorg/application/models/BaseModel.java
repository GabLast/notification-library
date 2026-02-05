package org.myorg.application.models;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@SuperBuilder
@Getter
@Setter
public abstract class BaseModel implements Serializable {

    protected Long id = 0L;

//    protected String createdBy = "system";
//
//    protected String modifiedBy = "system";
//
//    protected LocalDateTime dateCreated = LocalDateTime.now();
//
//    protected LocalDateTime lastUpdated = LocalDateTime.now();
//
//    protected Long version = 0L;
//
//    protected boolean enabled = true;

}
