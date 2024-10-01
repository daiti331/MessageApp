package com.example.messageapp.servicce;

import com.example.messageapp.entity.Profile;
import com.example.messageapp.entity.User;

public interface ProfileService {
	void createProfile(User user); // ユーザー登録メソッドの宣言
	void updateProfile(Profile profile); // プロフィール更新メソッドの宣言
}
