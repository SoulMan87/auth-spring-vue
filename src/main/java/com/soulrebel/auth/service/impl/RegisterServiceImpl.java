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
import com.soulrebel.auth.service.Token;
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
    public LoginResponse login(final LoginRequest loginRequest) {

        final var token = generateToken (loginRequest.email (), loginRequest.password ());
        return new LoginResponse (token.getToken ());
    }

    private Token generateToken(final String email, final String password) {
        final var user = repository.findByEmail (email)
                .orElseThrow (InvalidCredentialsError::new);

        if (!encoder.matches (password, user.getPassword ()))
            throw new InvalidCredentialsError ();
        return Token.of (user.getId (), 10L,
                "very_long_and_secure_and_safe_access_key");
    }

    @Override
    public RegisterResponse registerUser(final RegisterRequest registerRequest) {
        validatePasswordMatching (registerRequest);

        final var user = createAndSave (registerRequest);

        return new RegisterResponse (user.getId (), user.getFirstName (), user.getLastName (), user.getEmail ());
    }

    private void validatePasswordMatching(final RegisterRequest registerRequest) {
        if (!Objects.equals (registerRequest.password (), registerRequest.passwordConfirm ()))
            throw new PasswordsDontMatchError ();
    }

    private User createAndSave(final RegisterRequest registerRequest) {
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
