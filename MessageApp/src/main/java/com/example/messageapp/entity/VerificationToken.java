//package com.example.messageapp.entity;
//
//import java.util.Date;
//
//import jakarta.persistence.Entity;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
//import jakarta.persistence.Id;
//import jakarta.persistence.OneToOne;
//
//import lombok.Data;
//
////変更開始-終了	
//@Entity
//@Data
//public class VerificationToken {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    private String token;
//
//    @OneToOne
//    private User user;
//
//    private Date expiryDate;
//
//    // コンストラクタやゲッター・セッターを追加
//    public VerificationToken(String token, User user) {
//        this.token = token;
//        this.user = user;
//        this.expiryDate = new Date(System.currentTimeMillis() + 3600000); // 1時間の有効期限
//    }
//}