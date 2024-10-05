package com.example.messageapp.service;

import java.util.List;
import java.util.Optional;

import com.example.messageapp.entity.User;

public interface UserService {
    void registerUser(User user); // ユーザー登録メソッドの宣言
    User findByUsername(String username);
    Optional<User> findById(Long id);  // ユーザーをIDで検索するメソッドを追加
    List<User> findAllUsersExcept(String username); // 新たに追加
    void populateUserProfiles(List<User> users); // 新たに追加

}