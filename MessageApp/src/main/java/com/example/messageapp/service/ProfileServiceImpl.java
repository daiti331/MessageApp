package com.example.messageapp.service;

import org.springframework.stereotype.Service;

import com.example.messageapp.entity.Profile;
import com.example.messageapp.repository.ProfileRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
    private final ProfileRepository profileRepository; // ProfileRepositoryはJPAリポジトリ

	@Override
	public void createProfile(Profile profile) {
		
		try {
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
	
    @Override
    public Profile findByUserId(Long userId) {
        return profileRepository.findByUserId(userId); // プロフィールを取得
    }

}
