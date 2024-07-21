package com.auth.boilerplate.user.dto;

import com.auth.boilerplate.user.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JoinRequest {

    private String email;
    private String password;


    public User toUser() {
        return User.builder()
                .email(email)
                .password(password)
                .build();
    }
}
