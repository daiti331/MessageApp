package com.example.messageapp.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

import lombok.Data;
	
@Entity
@Data
public class VerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String token;

    @OneToOne
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    @Column(nullable = false)
    private LocalDateTime expiryDate; // 有効期限のカラム
    
    // 引数なしのデフォルトコンストラクタ
    public VerificationToken() {
    }

    // コンストラクタやゲッター・セッターを追加
    public VerificationToken(String token, User user) {
        this.token = token;
        this.user = user;
        this.expiryDate = LocalDateTime.now().plusMinutes(60); // 60分の有効期限
    }
    // 有効期限の確認メソッド
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiryDate);
    }
}