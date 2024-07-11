package com.auth.boilerplate.common.dto;

import com.auth.boilerplate.common.dto.CommonResponse.CommonData;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class CommonResponse<T> extends ResponseEntity<CommonData<T>> {
    public CommonResponse(T data, HttpStatus status) {
        super(new CommonData<>(data), status);
    }

    public CommonResponse(HttpStatus status) {
        super(status);
    }

    public record CommonData<T>(
            @JsonProperty("data") T data
    ) {
    }
}
