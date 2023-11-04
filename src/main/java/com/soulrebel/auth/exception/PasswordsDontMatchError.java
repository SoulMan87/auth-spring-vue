package com.soulrebel.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class PasswordsDontMatchError extends ResponseStatusException {
    public PasswordsDontMatchError() {
        super (HttpStatus.BAD_REQUEST, "Passwords do not match");
    }
}
