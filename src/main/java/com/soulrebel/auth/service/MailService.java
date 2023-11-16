package com.soulrebel.auth.service;

import org.springframework.stereotype.Component;

@Component
public interface MailService {
    void sendForgotMessage(String email, String token, String baseUrl);
}
