package com.soulrebel.auth.service;

import com.soulrebel.auth.domain.LoginRequest;
import com.soulrebel.auth.domain.LoginResponse;
import com.soulrebel.auth.domain.RegisterRequest;
import com.soulrebel.auth.domain.RegisterResponse;
import org.springframework.stereotype.Component;

@Component
public interface RegisterService {

    LoginResponse login(LoginRequest loginRequest);

    RegisterResponse registerUser(RegisterRequest registerRequest);

}
