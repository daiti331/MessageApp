package com.example.messageapp.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.messageapp.entity.User;
import com.example.messageapp.entity.VerificationToken;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    VerificationToken findByToken(String token);
    List<VerificationToken> findByExpiryDateBefore(LocalDateTime dateTime);
    
    // ユーザーに関連するトークンを取得するメソッド
    VerificationToken findByUser(User user);
}