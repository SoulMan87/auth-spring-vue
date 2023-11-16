package com.soulrebel.auth.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ResetRequest(String token, String password,
                           @JsonProperty(value = "password_confirm") String passwordConfirm) {
}
