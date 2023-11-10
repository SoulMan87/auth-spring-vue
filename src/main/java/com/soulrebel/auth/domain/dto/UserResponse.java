package com.soulrebel.auth.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserResponse(
        Long id,
        @JsonProperty("first_name")
        String firstName,
        @JsonProperty("last_name")
        String lastName,
        String email) {
}
