package com.soulrebel.auth.domain;

import com.google.common.io.BaseEncoding;
import lombok.Getter;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Getter
public class Login {

    private static final Long ACCESS_TOKEN_VALIDITY = 1L;
    private static final Long REFRESH_TOKEN_VALIDITY = 1440L;

    private final Jwt accessJwt;
    private final Jwt refreshJwt;
    private final String otpSecret;
    private final String otpUrl;

    private Login(Jwt accessJwt, Jwt refreshJwt, String otpSecret, String otpUrl) {
        this.accessJwt = accessJwt;
        this.refreshJwt = refreshJwt;
        this.otpSecret = otpSecret;
        this.otpUrl = otpUrl;
    }

    public static Login of(Long userId, String accessSecret, String refreshSecret) {
        var otpSecret = generateOptSecret ();
        return new Login (
                Jwt.of (userId, ACCESS_TOKEN_VALIDITY, accessSecret),
                Jwt.of (userId, REFRESH_TOKEN_VALIDITY, refreshSecret),
                otpSecret, getOtpUrl (otpSecret));
    }

    public static Login of(Long userId, String accessSecret, Jwt refreshJwt) {
        var otpSecret = generateOptSecret ();
        return new Login (
                Jwt.of (userId, 1L, accessSecret),
                refreshJwt,
                otpSecret, getOtpUrl (otpSecret));
    }

    private static String generateOptSecret() {
        var uuid = UUID.randomUUID ().toString ();
        return BaseEncoding.base32 ().encode (uuid.getBytes (StandardCharsets.UTF_8));
    }

    private static String getOtpUrl(String otpSecret) {
        var appName = "My%20App";
        return String.format ("otpauth://totp/%s:Secret?secret=%s&issuer=%s", appName, otpSecret, appName);
    }
}
