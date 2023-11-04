package com.soulrebel.auth.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RegisterResponse(

        Long id,
        @JsonProperty("first_name")
        String firstName,
        @JsonProperty("last_name")
        String lastName,
        String email) {
}
