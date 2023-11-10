package com.soulrebel.auth.service;

import com.soulrebel.auth.domain.Login;
import com.soulrebel.auth.domain.dto.LoginRequest;
import com.soulrebel.auth.domain.dto.LoginResponse;
import com.soulrebel.auth.domain.dto.LogoutResponse;
import com.soulrebel.auth.domain.dto.RegisterRequest;
import com.soulrebel.auth.domain.dto.RegisterResponse;
import com.soulrebel.auth.domain.User;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;

@Component
public interface RegisterService {


    LoginResponse login(LoginRequest loginRequest, HttpServletResponse response);

    RegisterResponse registerUser(RegisterRequest registerRequest);

    User getUserFromToken(String token);

    Login refreshAccess(String refreshToken);

    LogoutResponse logout(HttpServletResponse response);
}
