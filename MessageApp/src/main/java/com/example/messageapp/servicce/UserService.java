package com.example.messageapp.servicce;

import java.util.Optional;

import com.example.messageapp.entity.User;

public interface UserService {
    void registerUser(User user); // ユーザー登録メソッドの宣言
    User findByUsername(String username);
    Optional<User> findById(Long id);  // ユーザーをIDで検索するメソッドを追加
}