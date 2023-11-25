package com.soulrebel.auth.domain.dto;

public record TwoFactorRequest(Long id, String secret, String code) {
}
