package com.example.messageapp.service;

import com.example.messageapp.entity.Profile;
import com.example.messageapp.entity.User;

public interface ProfileService {
    Profile findByUserId(Long userId); // ユーザーIDに基づいてプロフィールを取得するメソッド
	void createProfile(User user); // ユーザー登録メソッドの宣言
	void updateProfile(Profile profile); // プロフィール更新メソッドの宣言
}
