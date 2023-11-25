package com.soulrebel.auth.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LoginResponse(Long id, String secret, @JsonProperty("otpauth_url") String otpAuthUrl) {
}
