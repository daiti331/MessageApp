package com.example.messageapp.form;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.Data;

@Data
public class ProfileForm {
	
	private Long id; // プロフィールのIDを追加

    // 表示名の追加
    @NotNull
    @Size(min = 1, max = 30, message = "表示名は1文字以上30文字以下で入力してください。")
    private String displayName;

    @Size(max = 100, message = "自己紹介は100文字以内で入力してください。")
    private String bio;

    private Integer age;
    private String location;
    private String gender;
}