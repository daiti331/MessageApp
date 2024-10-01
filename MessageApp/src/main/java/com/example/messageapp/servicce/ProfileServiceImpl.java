package com.example.messageapp.servicce;

import org.springframework.stereotype.Service;

import com.example.messageapp.entity.Profile;
import com.example.messageapp.entity.User;
import com.example.messageapp.repository.ProfileRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
    private final ProfileRepository profileRepository; // ProfileRepositoryはJPAリポジトリ

	@Override
	public void createProfile(User user) {
		
		try {
        Profile profile = new Profile();
        profile.setUser(user); // Userオブジェクトをセット
        // 必要に応じて他のフィールドも設定
        profileRepository.save(profile); // プロフィールを保存
		} catch (Exception e) {
			// エラーが発生した場合のログ出力
	        System.err.println("プロフィールの作成に失敗しました: " + e.getMessage());
		}
	}

	@Override
	public void updateProfile(Profile profile) {
		// プロフィールをリポジトリに保存（更新）
        profileRepository.save(profile);
	}

}
