package com.auth.boilerplate.test.controller;

import com.auth.boilerplate.common.dto.CommonResponse;
import com.auth.boilerplate.common.handler.ResponseHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/test")
@RequiredArgsConstructor
public class TestController {

    @GetMapping("/unauth")
    public CommonResponse<?> callUnauth() {
        return ResponseHandler.handleResponse(HttpStatus.OK);
    }

    @GetMapping("/auth")
    public CommonResponse<?> callAuth() {
        return ResponseHandler.handleResponse(HttpStatus.OK);
    }

}
