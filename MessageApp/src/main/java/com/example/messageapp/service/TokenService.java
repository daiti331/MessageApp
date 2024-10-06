//package com.example.messageapp.service;
//
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.stereotype.Service;
////変更開始ー変更終了
//@Service
//public class TokenService {
//    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//
//    // トークンを生成
//    public String generateToken(String token) {
//        return passwordEncoder.encode(token);
//    }
//
//    // トークンを検証
//    public boolean validateToken(String plainToken, String hashedToken) {
//        return passwordEncoder.matches(plainToken, hashedToken);
//    }
//}