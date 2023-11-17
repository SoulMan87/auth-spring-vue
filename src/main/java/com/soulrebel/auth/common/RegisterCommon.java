package com.soulrebel.auth.common;

import com.soulrebel.auth.domain.Jwt;
import com.soulrebel.auth.domain.Login;
import com.soulrebel.auth.domain.Token;
import com.soulrebel.auth.domain.User;
import com.soulrebel.auth.domain.dto.PasswordRecovery;
import com.soulrebel.auth.domain.dto.RegisterRequest;
import com.soulrebel.auth.exception.EmailAlreadyExistsError;
import com.soulrebel.auth.exception.InvalidCredentialsError;
import com.soulrebel.auth.exception.InvalidLinkError;
import com.soulrebel.auth.exception.PasswordsDontMatchError;
import com.soulrebel.auth.exception.UnauthenticatedError;
import com.soulrebel.auth.exception.UserNotFoundError;
import com.soulrebel.auth.repository.UserRepository;
import com.soulrebel.auth.service.MailService;
import org.springframework.data.relational.core.conversion.DbActionExecutionException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;
import java.util.UUID;

@Component
public abstract class RegisterCommon {

    protected final String REFRESH_TOKEN = "refresh_token";
    protected final String SUCCESS = "success";
    protected final String FAILURE = "failure";
    protected final String ORIGIN = "Origin";
    protected final String PASSWORD_SUCCESSFULLY = "Password successfully";
    protected final String RESET_SUCCESSFULLY = "Reset successfully";

    protected final UserRepository repository;
    protected final PasswordEncoder encoder;
    protected final String accessTokenSecret;
    protected final String refreshTokenSecret;

    protected final MailService mailService;


    protected RegisterCommon(UserRepository repository, PasswordEncoder encoder,
                             String accessTokenSecret,
                             String refreshTokenSecret, MailService mailService) {
        this.repository = repository;
        this.encoder = encoder;
        this.accessTokenSecret = accessTokenSecret;
        this.refreshTokenSecret = refreshTokenSecret;
        this.mailService = mailService;
    }

    protected void resetLogic(final String token, final String newPassword) {
        final var user = repository.findByPasswordRecoveriesToken (token)
                .orElseThrow (InvalidLinkError::new);
        user.setPassword (encoder.encode (newPassword));
        user.removePasswordRecoveryIf (passwordRecovery -> Objects.equals (passwordRecovery.token (), token));
        repository.save (user);
    }

    protected void forgotLogic(final String email, final String originEmail) {
        var token = UUID.randomUUID ().toString ().replace ("-", "");
        var user = repository.findByEmail (email)
                .orElseThrow (UserNotFoundError::new);

        user.addPasswordRecovery (new PasswordRecovery (token));

        mailService.sendForgotMessage (email, token, originEmail);

        repository.save (user);
    }

    protected Boolean logoutLogic(final String refreshToken) {
        final var refreshJwt = Jwt.from (refreshToken, refreshTokenSecret);

        final var user = repository.findById (refreshJwt.getUserId ())
                .orElseThrow (UnauthenticatedError::new);
        final var tokenIsRemoved = user.removeTokenIf (token ->
                Objects.equals (token.refreshToken (), refreshToken));

        if (Boolean.FALSE.equals (tokenIsRemoved))
            repository.save (user);

        return tokenIsRemoved;
    }

    protected Login generateToken(final String email, final String password) {
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

    protected void setRefreshTokenCookie(final HttpServletResponse response, final String refreshToken) {
        final Cookie cookie = new Cookie (REFRESH_TOKEN, refreshToken);
        cookie.setMaxAge (3600);
        cookie.setHttpOnly (true);
        cookie.setPath ("/api");
        response.addCookie (cookie);
    }

    protected void validatePasswordMatching(final RegisterRequest registerRequest) {
        if (!Objects.equals (registerRequest.password (), registerRequest.passwordConfirm ()))
            throw new PasswordsDontMatchError ();
    }

    protected User createAndSave(final RegisterRequest registerRequest) {
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
