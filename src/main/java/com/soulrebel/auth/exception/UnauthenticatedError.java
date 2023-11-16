package com.soulrebel.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UnauthenticatedError  extends ResponseStatusException {
    public UnauthenticatedError() {
        super (HttpStatus.UNAUTHORIZED, "unauthenticated");
    }
}
