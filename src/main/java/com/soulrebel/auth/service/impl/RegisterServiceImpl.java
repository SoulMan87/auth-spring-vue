package com.soulrebel.auth.service.impl;

import com.soulrebel.auth.domain.RegisterRequest;
import com.soulrebel.auth.domain.RegisterResponse;
import com.soulrebel.auth.domain.User;
import com.soulrebel.auth.repository.UserRepository;
import com.soulrebel.auth.service.RegisterService;
import org.springframework.stereotype.Service;

@Service
public class RegisterServiceImpl implements RegisterService {

    private final UserRepository repository;

    public RegisterServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public RegisterResponse registerUser(RegisterRequest registerRequest) {

        var user = repository.save (User.of (
                registerRequest.firstName (),
                registerRequest.lastName (),
                registerRequest.email (),
                registerRequest.password ()
        ));
        return new RegisterResponse (user.getId (), user.getFirstName (), user.getLastName (), user.getEmail ());
    }
}
