package com.soulrebel.auth.service.impl;

import com.soulrebel.auth.domain.RegisterRequest;
import com.soulrebel.auth.domain.RegisterResponse;
import com.soulrebel.auth.domain.User;
import com.soulrebel.auth.repository.UserRepository;
import com.soulrebel.auth.service.RegisterService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

@Service
public class RegisterServiceImpl implements RegisterService {

    private final UserRepository repository;

    public RegisterServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public RegisterResponse registerUser(RegisterRequest registerRequest) {

        if (!Objects.equals (registerRequest.password (), registerRequest.passwordConfirm ()))
            throw new ResponseStatusException (HttpStatus.BAD_REQUEST, "password do not match");

        var user = repository.save (User.of (
                registerRequest.firstName (),
                registerRequest.lastName (),
                registerRequest.email (),
                registerRequest.password ()
        ));
        return new RegisterResponse (user.getId (), user.getFirstName (), user.getLastName (), user.getEmail ());
    }
}
