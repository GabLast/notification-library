package org.myorg.application.services.security;

import lombok.Getter;
import org.myorg.application.models.security.User;

import java.util.logging.Logger;

public class UserService {

    private static final Logger              LOGGER = Logger.getLogger(UserService.class.getName());
    private static       UserService instance;

    @Getter
    private User administrator;

    public UserService() {
        this.administrator = User.builder()
                .id(1L)
                .name("Administrator").email("mail1@mail.com")
                .phoneNumber("+18292421552").build();
    }

    public static synchronized UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }
}
