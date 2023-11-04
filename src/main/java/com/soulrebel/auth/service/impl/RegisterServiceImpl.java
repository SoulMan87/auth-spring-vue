package com.soulrebel.auth.service.impl;

import com.soulrebel.auth.domain.LoginRequest;
import com.soulrebel.auth.domain.LoginResponse;
import com.soulrebel.auth.domain.RegisterRequest;
import com.soulrebel.auth.domain.RegisterResponse;
import com.soulrebel.auth.domain.User;
import com.soulrebel.auth.exception.EmailAlreadyExistsError;
import com.soulrebel.auth.exception.InvalidCredentialsError;
import com.soulrebel.auth.exception.PasswordsDontMatchError;
import com.soulrebel.auth.repository.UserRepository;
import com.soulrebel.auth.service.RegisterService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.relational.core.conversion.DbActionExecutionException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RegisterServiceImpl implements RegisterService {

    private final UserRepository repository;

    private final PasswordEncoder encoder;

    @Override
    public RegisterResponse registerUser(RegisterRequest registerRequest) {
        validatePasswordMatching (registerRequest);

        var user = createAndSave (registerRequest);

        /*user = repository.save (User.of (
                registerRequest.firstName (),
                registerRequest.lastName (),
                registerRequest.email (),
                encoder.encode (registerRequest.password ())
        ));*/
        return new RegisterResponse (user.getId (), user.getFirstName (), user.getLastName (), user.getEmail ());
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {

        var user = repository.findByEmail (loginRequest.email ())
                .orElseThrow (InvalidCredentialsError::new);

        if (!encoder.matches (loginRequest.password (), user.getPassword ()))
            throw new InvalidCredentialsError ();

        return new LoginResponse (user.getId (), user.getFirstName (), user.getLastName (), user.getEmail ());
    }

    private void validatePasswordMatching(RegisterRequest registerRequest) {
        if (!Objects.equals (registerRequest.password (), registerRequest.passwordConfirm ()))
            throw new PasswordsDontMatchError ();
    }

    private User createAndSave(RegisterRequest registerRequest) {
        try {
            final var encodedPassword = encoder.encode (registerRequest.password ());
            final var user = User.of (
                    registerRequest.firstName (),
                    registerRequest.lastName (),
                    registerRequest.email (),
                    encodedPassword
            );
            return repository.save (user);
        } catch (DbActionExecutionException exception) {
            throw new EmailAlreadyExistsError ();
        }
    }
}
