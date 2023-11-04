package com.soulrebel.auth.controller;

import com.soulrebel.auth.domain.LoginRequest;
import com.soulrebel.auth.domain.LoginResponse;
import com.soulrebel.auth.domain.RegisterRequest;
import com.soulrebel.auth.domain.RegisterResponse;
import com.soulrebel.auth.service.RegisterService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AuthController {

    private final RegisterService registerService;

    @PostMapping(value = "/register")
    public RegisterResponse register(@RequestBody RegisterRequest registerRequest) {
        return new ResponseEntity<> (registerService.registerUser (registerRequest), HttpStatus.OK).getBody ();
    }

    @PostMapping(value = "/login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest) {
        return new ResponseEntity<> (registerService.login (loginRequest), HttpStatus.OK).getBody ();
    }
}
