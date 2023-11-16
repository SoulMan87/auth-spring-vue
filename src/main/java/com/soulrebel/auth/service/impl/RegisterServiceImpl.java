package com.soulrebel.auth.service.impl;

import com.soulrebel.auth.domain.Jwt;
import com.soulrebel.auth.domain.Login;
import com.soulrebel.auth.domain.Token;
import com.soulrebel.auth.domain.User;
import com.soulrebel.auth.domain.dto.ForgotRequest;
import com.soulrebel.auth.domain.dto.ForgotResponse;
import com.soulrebel.auth.domain.dto.LoginRequest;
import com.soulrebel.auth.domain.dto.LoginResponse;
import com.soulrebel.auth.domain.dto.LogoutResponse;
import com.soulrebel.auth.domain.dto.PasswordRecovery;
import com.soulrebel.auth.domain.dto.RegisterRequest;
import com.soulrebel.auth.domain.dto.RegisterResponse;
import com.soulrebel.auth.exception.EmailAlreadyExistsError;
import com.soulrebel.auth.exception.InvalidCredentialsError;
import com.soulrebel.auth.exception.PasswordsDontMatchError;
import com.soulrebel.auth.exception.UnauthenticatedError;
import com.soulrebel.auth.exception.UserNotFoundError;
import com.soulrebel.auth.repository.UserRepository;
import com.soulrebel.auth.service.MailService;
import com.soulrebel.auth.service.RegisterService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.relational.core.conversion.DbActionExecutionException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;
import java.util.UUID;

@Service

public class RegisterServiceImpl implements RegisterService {

    private final UserRepository repository;
    private final PasswordEncoder encoder;
    private final String accessTokenSecret;
    private final String refreshTokenSecret;

    private final MailService mailService;


    public RegisterServiceImpl(UserRepository repository, PasswordEncoder encoder,
                               @Value("${application.security.access-token-secret}")
                               String accessTokenSecret,
                               @Value("${application.security.refresh-token-secret}")
                               String refreshTokenSecret, MailService mailService) {
        this.repository = repository;
        this.encoder = encoder;
        this.accessTokenSecret = accessTokenSecret;
        this.refreshTokenSecret = refreshTokenSecret;
        this.mailService = mailService;
    }

    @Override
    public LoginResponse login(final LoginRequest loginRequest, final HttpServletResponse response) {
        final var login = generateToken (loginRequest.email (), loginRequest.password ());
        setRefreshTokenCookie (response, login.getRefreshJwt ().getToken ());
        return new LoginResponse (login.getAccessJwt ().getToken ());
    }

    @Override
    public RegisterResponse registerUser(final RegisterRequest registerRequest) {
        validatePasswordMatching (registerRequest);

        final var user = createAndSave (registerRequest);

        return new RegisterResponse (user.getId (), user.getFirstName (), user.getLastName (), user.getEmail ());
    }

    @Override
    public User getUserFromToken(final String token) {
        return repository.findById (Jwt.from (token, accessTokenSecret).getUserId ())
                .orElseThrow (UserNotFoundError::new);
    }

    @Override
    public Login refreshAccess(final String refreshToken) {

        final var refreshJwt = Jwt.from (refreshToken, refreshTokenSecret);
        final var user = repository.findByIdAndTokensRefreshTokenAndTokenExpiredAtGreaterThan
                        (refreshJwt.getUserId (), refreshJwt.getToken (), refreshJwt.getExpiration ())
                .orElseThrow (UnauthenticatedError::new);
        return Login.of (refreshJwt.getUserId (), accessTokenSecret, refreshJwt);
    }

    @Override
    public LogoutResponse logout(final HttpServletResponse response, final String refreshToken) {
        final var tokenIsRemoved = logoutLogic (refreshToken);

        Cookie cookie = new Cookie ("refresh_token", null);
        cookie.setMaxAge (0);
        cookie.setHttpOnly (true);

        response.addCookie (cookie);
        return Boolean.TRUE.equals (tokenIsRemoved) ? new LogoutResponse ("success") : new LogoutResponse ("failure");
    }

    @Override
    public ForgotResponse forgot(final ForgotRequest forgotRequest, final HttpServletRequest request) {
        final var originUrl = request.getHeader ("Origin");

        final var email = forgotRequest.email ();

        forgotLogic (email, originUrl);

        return new ForgotResponse ("Password recovery initiated successfully");
    }

    private void forgotLogic(final String email, final String originEmail) {
        var token = UUID.randomUUID ().toString ().replace ("-", "");
        var user = repository.findByEmail (email)
                .orElseThrow (UserNotFoundError::new);

        user.addPasswordRecovery (new PasswordRecovery (token));

        mailService.sendForgotMessage (email, token, originEmail);

        repository.save (user);
    }

    private Boolean logoutLogic(final String refreshToken) {
        final var refreshJwt = Jwt.from (refreshToken, refreshTokenSecret);

        final var user = repository.findById (refreshJwt.getUserId ())
                .orElseThrow (UnauthenticatedError::new);
        final var tokenIsRemoved = user.removeTokenIf (token ->
                Objects.equals (token.refreshToken (), refreshToken));

        if (Boolean.FALSE.equals (tokenIsRemoved))
            repository.save (user);

        return tokenIsRemoved;
    }

    private Login generateToken(final String email, final String password) {
        final var user = repository.findByEmail (email)
                .orElseThrow (InvalidCredentialsError::new);

        if (!encoder.matches (password, user.getPassword ()))
            throw new InvalidCredentialsError ();

        final var login = Login.of (user.getId (), accessTokenSecret, refreshTokenSecret);

        final var refreshJwt = login.getRefreshJwt ();

        user.addToken (new Token (refreshJwt.getToken (), refreshJwt.getIssuedAt (), refreshJwt.getExpiration ()));

        repository.save (user);

        return login;
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
