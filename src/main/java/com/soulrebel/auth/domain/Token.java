package com.soulrebel.auth.domain;

import java.time.LocalDateTime;

public record Token(String refreshToken, LocalDateTime issuedAt, LocalDateTime expiredAt) {
}
