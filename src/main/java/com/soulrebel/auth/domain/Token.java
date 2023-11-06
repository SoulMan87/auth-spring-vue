package com.soulrebel.auth.domain;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;

import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;

@Getter
public class Token {

    private final String token;

    private Token(String token) {
        this.token = token;
    }

    public static Token of(Long userId, Long validityInMinutes, String secretKey) {
        var issuedDate = Instant.now ();
        return new Token (
                Jwts.builder ()
                        .claim ("user_id", userId)
                        .setIssuedAt (Date.from (issuedDate))
                        .setExpiration (Date.from (issuedDate.plus (validityInMinutes, ChronoUnit.MINUTES)))
                        .signWith (SignatureAlgorithm.HS256, Base64.getEncoder ()
                                .encodeToString (secretKey.getBytes (StandardCharsets.UTF_8)))
                        .compact ()
        );
    }

    public static Token of(String token) {
        return new Token (token);
    }

    public static Long from(String token, String secretKey) {
        return ((Claims) Jwts.parserBuilder ()
                .setSigningKey (Base64.getEncoder ().encodeToString (secretKey.getBytes (StandardCharsets.UTF_8)))
                .build ()
                .parse (token)
                .getBody ())
                .get ("user_id", Long.class);
    }
}
