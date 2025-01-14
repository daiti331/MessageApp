package com.example.messageapp.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender emailSender;

    public void sendConfirmationEmail(String to, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("メールアドレス確認");
        message.setText("以下のリンクをクリックしてメールアドレスを確認してください:\n"
                + "http://localhost:8080/confirm?token=" + token);
        emailSender.send(message);
    }
}