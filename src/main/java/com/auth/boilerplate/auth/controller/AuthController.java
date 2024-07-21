package com.auth.boilerplate.auth.controller;

import static com.auth.boilerplate.common.handler.ResponseHandler.handleResponse;

import com.auth.boilerplate.auth.dto.EmailAuthRequest;
import com.auth.boilerplate.auth.dto.LoginRequest;
import com.auth.boilerplate.auth.dto.LoginSuccessResult;
import com.auth.boilerplate.auth.service.AuthService;
import com.auth.boilerplate.common.dto.CommonResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/mail")
    public CommonResponse<?> sendCode(
            @RequestBody EmailAuthRequest emailAuthRequest
    ) {
        authService.sendMail(emailAuthRequest.email());
        return handleResponse(HttpStatus.CREATED);
    }

    @GetMapping("/mail")
    public CommonResponse<?> authenticateCode(
            @RequestParam("email") String email,
            @RequestParam("code") String code
    ) {
        authService.authenticateCode(email, code);
        return handleResponse(HttpStatus.OK);
    }

    @PostMapping("/login")
    public CommonResponse<?> login(
            @RequestBody LoginRequest loginRequest,
            HttpServletResponse response
            ) throws UnsupportedEncodingException {

        LoginSuccessResult loginSuccessResult = authService.login(loginRequest);

        Cookie accessTokenCookie = new Cookie("Authorization", URLEncoder.encode(loginSuccessResult.getAccessToken(), "UTF-8"));
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(60 * 60 * 24);

        Cookie userIdCookie = new Cookie("user-id", String.valueOf(loginSuccessResult.getUserId()));
        userIdCookie.setHttpOnly(false);
        userIdCookie.setSecure(true);
        userIdCookie.setPath("/");
        userIdCookie.setMaxAge(60 * 60 * 24);

        response.addCookie(accessTokenCookie);
        response.addCookie(userIdCookie);

        return handleResponse(loginSuccessResult, HttpStatus.OK);
    }
}
