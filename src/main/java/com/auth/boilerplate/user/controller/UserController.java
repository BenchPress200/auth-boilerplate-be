package com.auth.boilerplate.user.controller;

import com.auth.boilerplate.common.dto.CommonResponse;
import com.auth.boilerplate.common.handler.ResponseHandler;
import com.auth.boilerplate.user.dto.JoinRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @RequestMapping("/join")
    public CommonResponse<?> join(@RequestBody JoinRequest joinRequest) {
        userService.join(joinRequest);
        return ResponseHandler.handleResponse(HttpStatus.CREATED);
    }
}
