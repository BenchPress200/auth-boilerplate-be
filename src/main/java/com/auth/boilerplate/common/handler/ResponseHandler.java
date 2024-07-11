package com.auth.boilerplate.common.handler;

import com.auth.boilerplate.common.dto.CommonResponse;
import org.springframework.http.HttpStatus;

public class ResponseHandler {
    public static <T> CommonResponse<T> handleResponse(T data, HttpStatus status) {
        return new CommonResponse<>(data, status);
    }

    public static <T> CommonResponse<T> handleResponse(HttpStatus status) {
        return new CommonResponse<>(status);
    }
}
