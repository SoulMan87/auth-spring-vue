package com.soulrebel.auth.service.impl;

import com.soulrebel.auth.common.RegisterCommon;
import com.soulrebel.auth.domain.Jwt;
import com.soulrebel.auth.domain.Login;
import com.soulrebel.auth.domain.User;
import com.soulrebel.auth.domain.dto.ForgotRequest;
import com.soulrebel.auth.domain.dto.ForgotResponse;
import com.soulrebel.auth.domain.dto.LoginRequest;
import com.soulrebel.auth.domain.dto.LoginResponse;
import com.soulrebel.auth.domain.dto.LogoutResponse;
import com.soulrebel.auth.domain.dto.RegisterRequest;
import com.soulrebel.auth.domain.dto.RegisterResponse;
import com.soulrebel.auth.domain.dto.ResetRequest;
import com.soulrebel.auth.domain.dto.ResetResponse;
import com.soulrebel.auth.exception.PasswordsDontMatchError;
import com.soulrebel.auth.exception.UnauthenticatedError;
import com.soulrebel.auth.exception.UserNotFoundError;
import com.soulrebel.auth.repository.UserRepository;
import com.soulrebel.auth.service.MailService;
import com.soulrebel.auth.service.RegisterService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

@Service
public class RegisterServiceImpl extends RegisterCommon implements RegisterService {


    protected RegisterServiceImpl(UserRepository repository, PasswordEncoder encoder,
                                  @Value("${application.security.access-token-secret}")
                                  String accessTokenSecret,
                                  @Value("${application.security.refresh-token-secret}")
                                  String refreshTokenSecret,
                                  MailService mailService) {
        super (repository, encoder, accessTokenSecret, refreshTokenSecret, mailService);
    }

    @Override
    public LoginResponse login(final LoginRequest loginRequest, final HttpServletResponse response) {
        final var login = generateToken (loginRequest.email (), loginRequest.password ());
        setRefreshTokenCookie (response, login.getRefreshJwt ().getToken ());
        return new LoginResponse (login.getAccessJwt ().getUserId (), login.getOtpSecret (), login.getOtpUrl ());
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

        Cookie cookie = new Cookie (REFRESH_TOKEN, null);
        cookie.setMaxAge (0);
        cookie.setHttpOnly (true);

        response.addCookie (cookie);
        return Boolean.TRUE.equals (tokenIsRemoved) ? new LogoutResponse (SUCCESS) : new LogoutResponse (FAILURE);
    }

    @Override
    public ForgotResponse forgot(final ForgotRequest forgotRequest, final HttpServletRequest request) {
        final var originUrl = request.getHeader (ORIGIN);

        final var email = forgotRequest.email ();

        forgotLogic (email, originUrl);

        return new ForgotResponse (PASSWORD_SUCCESSFULLY);
    }

    @Override
    public ResetResponse reset(final ResetRequest request) {

        if (!Objects.equals (request.password (), request.passwordConfirm ()))
            throw new PasswordsDontMatchError ();

        resetLogic (request.token (), request.password ());


        return new ResetResponse (RESET_SUCCESSFULLY);
    }
}
