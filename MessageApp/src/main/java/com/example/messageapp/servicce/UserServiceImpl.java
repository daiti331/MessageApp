package com.example.messageapp.servicce;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.messageapp.entity.User;
import com.example.messageapp.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	private final BCryptPasswordEncoder passwordEncoder; //パスワードをハッシュ化用
    private final UserRepository userRepository;

    // 新しいユーザーを登録するメソッド
    @Override
    public void registerUser(User user) {
    	user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user); // ユーザーを保存
    }

}