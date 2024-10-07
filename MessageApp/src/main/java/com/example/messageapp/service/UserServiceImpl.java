
package com.example.messageapp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.messageapp.entity.Profile;
import com.example.messageapp.entity.User;
import com.example.messageapp.entity.VerificationToken;
import com.example.messageapp.repository.ProfileRepository;
import com.example.messageapp.repository.UserRepository;
import com.example.messageapp.repository.VerificationTokenRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	private final BCryptPasswordEncoder passwordEncoder; //パスワードをハッシュ化用
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository; // プロフィールリポジトリをインジェクト
    //変更開始
    private final VerificationTokenRepository verificationTokenRepository;
    //変更終了



    // 新しいユーザーを登録するメソッド
    @Override
    public void registerUser(User user) {
    	user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user); // ユーザーを保存
    }
    
    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    @Override
    public List<User> findAllUsersExcept(String username) {
//        return userRepository.findAllByEnabledTrue().stream()
//            .filter(user -> !user.getUsername().equals(username))
//            .collect(Collectors.toList());
    	return userRepository.findAllByEnabledTrueAndUsernameNot(username);
    }
    
    @Override
    public void populateUserProfiles(List<User> users) {
        users.forEach(user -> {
            Profile profile = profileRepository.findByUserId(user.getId());
            user.setProfile(profile); // ユーザーにプロフィール情報を設定
        });
    }
    
    @Override
    public void createVerificationToken(User user, String token) {
        // トークンをデータベースに保存する処理を追加
        VerificationToken verificationToken = new VerificationToken(token, user);
        verificationTokenRepository.save(verificationToken);
    }

}
