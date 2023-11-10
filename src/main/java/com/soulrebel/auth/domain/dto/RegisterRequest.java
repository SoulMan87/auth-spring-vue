package com.soulrebel.auth.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RegisterRequest(
        @JsonProperty("first_name")
        String firstName,
        @JsonProperty("last_name")
        String lastName,
        String email, String password,
        @JsonProperty("password_confirm")
        String passwordConfirm) {
}
