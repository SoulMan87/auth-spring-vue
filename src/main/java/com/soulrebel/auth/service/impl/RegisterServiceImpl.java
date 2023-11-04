package com.soulrebel.auth.service.impl;

import com.soulrebel.auth.domain.LoginRequest;
import com.soulrebel.auth.domain.LoginResponse;
import com.soulrebel.auth.domain.RegisterRequest;
import com.soulrebel.auth.domain.RegisterResponse;
import com.soulrebel.auth.domain.User;
import com.soulrebel.auth.repository.UserRepository;
import com.soulrebel.auth.service.RegisterService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RegisterServiceImpl implements RegisterService {

    private final UserRepository repository;

    private final PasswordEncoder encoder;

    @Override
    public RegisterResponse registerUser(RegisterRequest registerRequest) {

        if (!Objects.equals (registerRequest.password (), registerRequest.passwordConfirm ()))
            throw new ResponseStatusException (HttpStatus.BAD_REQUEST, "password do not match");

        var user = repository.save (User.of (
                registerRequest.firstName (),
                registerRequest.lastName (),
                registerRequest.email (),
                encoder.encode (registerRequest.password ())
        ));
        return new RegisterResponse (user.getId (), user.getFirstName (), user.getLastName (), user.getEmail ());
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {

        var user = repository.findByEmail (loginRequest.email ())
                .orElseThrow (() -> new ResponseStatusException (HttpStatus.BAD_REQUEST));

        if (!encoder.matches (loginRequest.password (), user.getPassword ()))
            throw new ResponseStatusException (HttpStatus.BAD_REQUEST, "invalid credentials");

        return new LoginResponse (user.getId (), user.getFirstName (), user.getLastName (), user.getEmail ());
    }
}
