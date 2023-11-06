package com.soulrebel.auth.domain;

import lombok.Getter;

@Getter
public class Login {

    private static final Long ACCESS_TOKEN_VALIDITY = 1L;
    private static final Long REFRESH_TOKEN_VALIDITY = 1440L;

    private final Token accessToken;
    private final Token refreshToken;

    private Login(Token accessToken, Token refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public static Login of(Long userId, String accessSecret, String refreshSecret) {
        return new Login (
                Token.of (userId, ACCESS_TOKEN_VALIDITY, accessSecret),
                Token.of (userId, REFRESH_TOKEN_VALIDITY, refreshSecret)
        );
    }

    public static Login of(Long userId, String accessSecret, Token refreshToken) {
        return new Login (
                Token.of (userId, 1L, accessSecret),
                refreshToken
        );
    }
}
