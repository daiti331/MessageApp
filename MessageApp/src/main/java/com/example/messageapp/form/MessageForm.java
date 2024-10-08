package com.example.messageapp.form;
import jakarta.validation.constraints.NotEmpty;

import lombok.Data;

//変更開始-変更終了
@Data
public class MessageForm {
    private Long recipientId;
    
    @NotEmpty(message = "メッセージ内容を入力してください")
    private String content;
}