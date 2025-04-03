package com.example.messageapp.service;

import com.example.messageapp.entity.Profile;

public interface ProfileService {
    Profile findByUserId(Long userId); // ユーザーIDに基づいてプロフィールを取得するメソッド
	void createProfile(Profile profile); // ユーザー登録メソッドの宣言
	void updateProfile(Profile profile); // プロフィール更新メソッドの宣言
}
