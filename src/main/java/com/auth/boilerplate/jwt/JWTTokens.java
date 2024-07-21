package com.auth.boilerplate.jwt;

public record JWTTokens(
        String accessToken,
        String refreshToken
) {
}
