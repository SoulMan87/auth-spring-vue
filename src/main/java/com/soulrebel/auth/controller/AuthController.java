package com.soulrebel.auth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AuthController {
    @GetMapping(value = "/hello")
    public String hello(){
        return "Hello!";
    }
}
