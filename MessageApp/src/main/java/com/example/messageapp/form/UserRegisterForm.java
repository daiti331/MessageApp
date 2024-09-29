package com.example.messageapp.form;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import lombok.Data;

@Data
public class UserRegisterForm {
    
    @NotEmpty(message = "ユーザー名は必須です")
    private String username;

    @NotEmpty(message = "メールアドレスは必須です")
    @Email(message = "正しいメールアドレスを入力してください")
    private String email;

    @NotEmpty(message = "パスワードは必須です")
    @Size(min = 6, message = "パスワードは6文字以上である必要があります")
    private String password;
}