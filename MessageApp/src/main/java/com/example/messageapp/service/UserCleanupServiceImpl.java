package com.example.messageapp.service;

import java.util.List;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.messageapp.entity.User;
import com.example.messageapp.entity.VerificationToken;
import com.example.messageapp.repository.ProfileRepository;
import com.example.messageapp.repository.UserRepository;
import com.example.messageapp.repository.VerificationTokenRepository;

@Service
public class UserCleanupServiceImpl implements UserCleanupService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;
    
    @Autowired
    private ProfileRepository profileRepository; // プロフィールリポジトリの追加

    // 1時間ごとに実行
    @Scheduled(fixedRate = 3600000)
    @Transactional
    @Override
    public void cleanupExpiredUsers() {
        
        // enabledがfalse（仮登録）のユーザーを取得
        List<User> users = userRepository.findAllByEnabledFalse(); // 仮登録のユーザーを取得
        
        for (User user : users) {
            VerificationToken token = verificationTokenRepository.findByUser(user); // ユーザーに関連するトークンを取得
            if (token.isExpired()) { // トークンが期限切れか確認
                verificationTokenRepository.delete(token); // トークンを削除
                profileRepository.deleteByUserId(user.getId()); //先にプロフィールを削除しないとユーザーを削除できない(関係ないかも)
                userRepository.delete(user);
            
            }
        }
    }

}
