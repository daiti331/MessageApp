package com.example.messageapp.form;
import jakarta.validation.constraints.NotBlank;

import lombok.Data;

//変更開始-変更終了
@Data
public class LoginForm {
	
    @NotBlank(message = "ユーザー名を入力してください")
    private String username;

    @NotBlank(message = "パスワードを入力してください")
    private String password;
}