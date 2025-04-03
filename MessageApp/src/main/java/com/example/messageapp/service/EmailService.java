package com.example.messageapp.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender emailSender;
    // application.yml で定義した base-url をインジェクション
    @Value("${custom.base-url}")
    private String baseUrl;

    public void sendConfirmationEmail(String to, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        String confirmationUrl = baseUrl + "/confirm?token=" + token; // base-urlを使ってリンク生成
        message.setTo(to);
        message.setSubject("メールアドレス確認");
        message.setText("以下のリンクをクリックしてメールアドレスを確認してください:\n"
                + confirmationUrl);
        message.setFrom("info@earth-sys.com");
        emailSender.send(message);
    }
}