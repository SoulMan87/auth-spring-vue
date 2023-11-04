package com.soulrebel.auth.service;

import com.soulrebel.auth.domain.RegisterRequest;
import com.soulrebel.auth.domain.RegisterResponse;
import org.springframework.stereotype.Component;

@Component
public interface RegisterService {

    RegisterResponse registerUser(RegisterRequest registerRequest);
}
