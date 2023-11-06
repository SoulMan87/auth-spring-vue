package com.soulrebel.auth.service.impl;

import com.soulrebel.auth.domain.Login;
import com.soulrebel.auth.domain.LoginRequest;
import com.soulrebel.auth.domain.LoginResponse;
import com.soulrebel.auth.domain.RegisterRequest;
import com.soulrebel.auth.domain.RegisterResponse;
import com.soulrebel.auth.domain.Token;
import com.soulrebel.auth.domain.User;
import com.soulrebel.auth.exception.EmailAlreadyExistsError;
import com.soulrebel.auth.exception.InvalidCredentialsError;
import com.soulrebel.auth.exception.PasswordsDontMatchError;
import com.soulrebel.auth.exception.UserNotFoundError;
import com.soulrebel.auth.repository.UserRepository;
import com.soulrebel.auth.service.RegisterService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.relational.core.conversion.DbActionExecutionException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

@Service

public class RegisterServiceImpl implements RegisterService {

    private final UserRepository repository;
    private final PasswordEncoder encoder;
    private final String accessTokenSecret;
    private final String refreshTokenSecret;

    public RegisterServiceImpl(UserRepository repository, PasswordEncoder encoder,
                               @Value("${application.security.access-token-secret}")
                               String accessTokenSecret,
                               @Value("${application.security.refresh-token-secret}")
                               String refreshTokenSecret) {
        this.repository = repository;
        this.encoder = encoder;
        this.accessTokenSecret = accessTokenSecret;
        this.refreshTokenSecret = refreshTokenSecret;
    }

    @Override
    public LoginResponse login(final LoginRequest loginRequest, final HttpServletResponse response) {
        final var login = generateToken (loginRequest.email (), loginRequest.password ());
        setRefreshTokenCookie (response, login.getRefreshToken ().getToken ());
        return new LoginResponse (login.getAccessToken ().getToken ());
    }

    @Override
    public RegisterResponse registerUser(final RegisterRequest registerRequest) {
        validatePasswordMatching (registerRequest);

        final var user = createAndSave (registerRequest);

        return new RegisterResponse (user.getId (), user.getFirstName (), user.getLastName (), user.getEmail ());
    }

    @Override
    public User getUserFromToken(String token) {
        return repository.findById (Token.from (token, accessTokenSecret))
                .orElseThrow (UserNotFoundError::new);
    }

    @Override
    public Login refreshAccess(String refreshToken) {

        var userId = Token.from (refreshToken, refreshTokenSecret);

        return Login.of (userId, accessTokenSecret, Token.of (refreshToken));
    }

    private Login generateToken(final String email, final String password) {
        final var user = repository.findByEmail (email)
                .orElseThrow (InvalidCredentialsError::new);

        if (!encoder.matches (password, user.getPassword ()))
            throw new InvalidCredentialsError ();
        return Login.of (user.getId (), accessTokenSecret, refreshTokenSecret);
    }

    private void setRefreshTokenCookie(final HttpServletResponse response, final String refreshToken) {
        final Cookie cookie = new Cookie ("refresh_token", refreshToken);
        cookie.setMaxAge (3600);
        cookie.setHttpOnly (true);
        cookie.setPath ("/api");
        response.addCookie (cookie);
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
