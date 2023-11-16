package com.soulrebel.auth.service.impl;

import com.soulrebel.auth.service.MailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailServiceImpl implements MailService {
    private final JavaMailSender mailSender;
    private final String defaultFrontEndUrl;

    public MailServiceImpl(JavaMailSender javaMailSender, @Value("${application.frontend.default.url}")
    String defaultFrontEndUrl) {
        this.mailSender = javaMailSender;
        this.defaultFrontEndUrl = defaultFrontEndUrl;
    }

    @Override
    public void sendForgotMessage(String email, String token, String baseUrl) {

        final var url = baseUrl != null ? baseUrl : defaultFrontEndUrl;

        SimpleMailMessage message = new SimpleMailMessage ();
        message.setFrom ("nonreply@something.com");
        message.setTo (email);
        message.setSubject ("Reset your password");
        message.setText (String.format ("Click <a href=\"%s/reset/%s\">here</a> to reset your password.", url, token));

        mailSender.send (message);

    }
}
