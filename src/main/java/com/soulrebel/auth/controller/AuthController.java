package com.soulrebel.auth.controller;

import com.soulrebel.auth.entity.User;
import com.soulrebel.auth.repository.UserRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final UserRepository repository;

    public AuthController(UserRepository repository) {
        this.repository = repository;
    }

    @GetMapping(value = "/hello")
    public String hello() {
        return "Hello!";
    }

    @PostMapping(value = "/register")
    public User register(@RequestBody User user) {
        return repository.save (user);
    }
}
