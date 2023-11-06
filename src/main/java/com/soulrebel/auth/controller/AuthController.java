package com.soulrebel.auth.controller;

import com.soulrebel.auth.domain.LoginRequest;
import com.soulrebel.auth.domain.LoginResponse;
import com.soulrebel.auth.domain.LogoutResponse;
import com.soulrebel.auth.domain.RefreshResponse;
import com.soulrebel.auth.domain.RegisterRequest;
import com.soulrebel.auth.domain.RegisterResponse;
import com.soulrebel.auth.domain.UserResponse;
import com.soulrebel.auth.service.RegisterService;
import com.soulrebel.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AuthController {

    private final RegisterService registerService;
    private final UserService userService;

    @PostMapping(value = "/register")
    public RegisterResponse register(@RequestBody RegisterRequest registerRequest) {
        return new ResponseEntity<> (registerService.registerUser (registerRequest), HttpStatus.OK).getBody ();
    }

    @PostMapping(value = "/login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        return new ResponseEntity<> (registerService.login (loginRequest, response), HttpStatus.OK).getBody ();
    }

    @GetMapping(value = "/user")
    public UserResponse user(HttpServletRequest request) {
        return new ResponseEntity<> (userService.createUser (request), HttpStatus.OK).getBody ();
    }

    @PostMapping(value = "/refresh")
    public RefreshResponse refresh(@CookieValue("refresh_token") String refreshToken) {
        return new RefreshResponse (registerService.refreshAccess (refreshToken).getRefreshToken ().getToken ());
    }

    @PostMapping(value = "/logout")
    public LogoutResponse logout(HttpServletResponse response) {
        return new ResponseEntity<> (registerService.logout (response), HttpStatus.OK).getBody ();
    }
}
