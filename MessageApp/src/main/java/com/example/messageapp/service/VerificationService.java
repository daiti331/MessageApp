package com.example.messageapp.service;

import com.example.messageapp.entity.VerificationToken;

public interface VerificationService {
    VerificationToken findByToken(String token);
    void enableUserAccount(VerificationToken verificationToken);
}