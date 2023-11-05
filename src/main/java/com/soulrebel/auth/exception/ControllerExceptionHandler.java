package com.soulrebel.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(InvalidCredentialsError.class)
    public ResponseEntity<ErrorMessage> resourceNotFoundException(InvalidCredentialsError exception
            , WebRequest request) {
        return new ResponseEntity<> (resourceNotFoundExceptionBuilder (exception, request), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EmailAlreadyExistsError.class)
    public ResponseEntity<ErrorMessage> emailAlreadyExistsError(EmailAlreadyExistsError exception
            , WebRequest request) {
        return new ResponseEntity<> (emailAlreadyExistsErrorBuilder (exception, request), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NoBearerTokenError.class)
    public ResponseEntity<ErrorMessage> noBearerTokenError(NoBearerTokenError exception
            , WebRequest request) {
        return new ResponseEntity<> (noBearerTokenErrorBuilder (exception, request), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserNotFoundError.class)
    public ResponseEntity<ErrorMessage> noBearerTokenError(UserNotFoundError exception
            , WebRequest request) {
        return new ResponseEntity<> (userNotFoundErrorBuilder (exception, request), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> globalExceptionHandler(Exception exception, WebRequest request) {
        return new ResponseEntity<> (errorMessageGlobalExceptionHandlerBuilder (exception, request),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ErrorMessage noBearerTokenErrorBuilder(Exception exception, WebRequest request) {
        return ErrorMessage.builder ()
                .statusCode (HttpStatus.INTERNAL_SERVER_ERROR.value ())
                .timestamp (new Date ())
                .message (exception.getMessage ())
                .description (request.getDescription (false))
                .build ();
    }

    private ErrorMessage userNotFoundErrorBuilder(Exception exception, WebRequest request) {
        return ErrorMessage.builder ()
                .statusCode (HttpStatus.INTERNAL_SERVER_ERROR.value ())
                .timestamp (new Date ())
                .message (exception.getMessage ())
                .description (request.getDescription (false))
                .build ();
    }

    private ErrorMessage errorMessageGlobalExceptionHandlerBuilder(Exception exception, WebRequest request) {
        return ErrorMessage.builder ()
                .statusCode (HttpStatus.INTERNAL_SERVER_ERROR.value ())
                .timestamp (new Date ())
                .message (exception.getMessage ())
                .description (request.getDescription (false))
                .build ();
    }

    private ErrorMessage resourceNotFoundExceptionBuilder(InvalidCredentialsError exception, WebRequest request) {
        return ErrorMessage.builder ()
                .statusCode (HttpStatus.NOT_FOUND.value ())
                .timestamp (new Date ())
                .message (exception.getMessage ())
                .description (request.getDescription (false))
                .build ();
    }

    private ErrorMessage emailAlreadyExistsErrorBuilder(EmailAlreadyExistsError exception, WebRequest request) {
        return ErrorMessage.builder ()
                .statusCode (HttpStatus.NOT_FOUND.value ())
                .timestamp (new Date ())
                .message (exception.getMessage ())
                .description (request.getDescription (false))
                .build ();
    }
}
