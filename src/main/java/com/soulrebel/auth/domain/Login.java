package com.soulrebel.auth.domain;

import dev.samstevens.totp.secret.DefaultSecretGenerator;
import lombok.Getter;

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

    public static Login of(Long userId, String accessSecret, String refreshSecret, Boolean generateOtp) {
        var otpSecret = Boolean.TRUE.equals (generateOtp) ? generateOptSecret () : null;
        var otpUrl = Boolean.TRUE.equals (generateOtp) ? getOtpUrl (otpSecret) : null;

        return new Login (
                Jwt.of (userId, ACCESS_TOKEN_VALIDITY, accessSecret),
                Jwt.of (userId, REFRESH_TOKEN_VALIDITY, refreshSecret),
                otpSecret,
                otpUrl
        );
    }

    public static Login of(Long userId, String accessSecret, Jwt refreshJwt, Boolean generateOtp) {
        var otpSecret = Boolean.TRUE.equals (generateOtp) ? generateOptSecret () : null;
        var otpUrl = Boolean.TRUE.equals (generateOtp) ? getOtpUrl (otpSecret) : null;

        return new Login (
                Jwt.of (userId, 1L, accessSecret),
                refreshJwt,
                otpSecret, otpUrl
        );
    }

    private static String generateOptSecret() {
        return new DefaultSecretGenerator ().generate ();
    }

    private static String getOtpUrl(String otpSecret) {
        var appName = "My%20App";
        return String.format ("otpauth://totp/%s:Secret?secret=%s&issuer=%s", appName, otpSecret, appName);
    }
}
