package com.example.messageapp.service;

import org.springframework.stereotype.Service;

import com.example.messageapp.entity.User;
import com.example.messageapp.entity.VerificationToken;
import com.example.messageapp.repository.UserRepository;
import com.example.messageapp.repository.VerificationTokenRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VerificationServiceImpl implements VerificationService {

    private final VerificationTokenRepository verificationTokenRepository;

    private final UserRepository userRepository;

    @Override
    public VerificationToken findByToken(String token) {
        return verificationTokenRepository.findByToken(token);
    }

    @Override
    public void enableUserAccount(VerificationToken verificationToken) {
        User user = verificationToken.getUser();
        user.setEnabled(true); // ユーザーアカウントを有効化
        userRepository.save(user); // ユーザー情報を保存
    }
}