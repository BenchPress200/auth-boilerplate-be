package com.auth.boilerplate.auth.exception;

import com.auth.boilerplate.common.dto.CommonResponse;
import com.auth.boilerplate.common.handler.ResponseHandler;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class AuthExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({AuthException.class})
    public CommonResponse<?> handleException(HttpServletRequest request, AuthException e) {
        log.error("Authentication Exception {} => ", e.getMessage());

        return ResponseHandler.handleResponse(e.getHttpStatus());
    }

}
