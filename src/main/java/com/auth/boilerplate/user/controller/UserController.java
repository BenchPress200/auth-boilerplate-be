package com.auth.boilerplate.user.controller;

import com.auth.boilerplate.common.dto.CommonResponse;
import com.auth.boilerplate.common.handler.ResponseHandler;
import com.auth.boilerplate.user.dto.JoinRequest;
import com.auth.boilerplate.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public CommonResponse<?> join(
            @RequestBody JoinRequest joinRequest
    ) {
        userService.join(joinRequest);
        return ResponseHandler.handleResponse(HttpStatus.CREATED);
    }


}
