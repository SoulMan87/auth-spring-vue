package com.soulrebel.auth.service;

import com.soulrebel.auth.domain.LoginRequest;
import com.soulrebel.auth.domain.LoginResponse;
import com.soulrebel.auth.domain.RegisterRequest;
import com.soulrebel.auth.domain.RegisterResponse;
import com.soulrebel.auth.domain.User;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;

@Component
public interface RegisterService {


    LoginResponse login(LoginRequest loginRequest, HttpServletResponse response);

    RegisterResponse registerUser(RegisterRequest registerRequest);

    User getUserFromToken(String token);
}
