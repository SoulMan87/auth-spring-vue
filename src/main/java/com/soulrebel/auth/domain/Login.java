package com.soulrebel.auth.domain;

import lombok.Getter;

@Getter
public class Login {

    private static final Long ACCESS_TOKEN_VALIDITY = 1L;
    private static final Long REFRESH_TOKEN_VALIDITY = 1440L;

    private final Jwt accessJwt;
    private final Jwt refreshJwt;

    private Login(Jwt accessJwt, Jwt refreshJwt) {
        this.accessJwt = accessJwt;
        this.refreshJwt = refreshJwt;
    }

    public static Login of(Long userId, String accessSecret, String refreshSecret) {
        return new Login (
                Jwt.of (userId, ACCESS_TOKEN_VALIDITY, accessSecret),
                Jwt.of (userId, REFRESH_TOKEN_VALIDITY, refreshSecret)
        );
    }

    public static Login of(Long userId, String accessSecret, Jwt refreshJwt) {
        return new Login (
                Jwt.of (userId, 1L, accessSecret),
                refreshJwt
        );
    }
}
