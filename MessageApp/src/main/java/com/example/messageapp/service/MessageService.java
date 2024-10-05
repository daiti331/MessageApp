package com.example.messageapp.service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import com.example.messageapp.entity.Message;
import com.example.messageapp.entity.User;

public interface MessageService {
    List<Message> getReceivedMessages(User user);
    List<Message> getSentMessages(User user);
    long getUnreadCount(User user);
    void sendMessage(User sender, User recipient, String content);
    Optional<Message> findById(Long id);  // メッセージIDに基づいてメッセージを取得するメソッドを定義
    void markAsRead(Message message); // 既読フラグを更新するメソッド
    void saveMessage(Message message); // メッセージを保存するメソッド
    
    
    long getUnreadCountForUser(Principal principal);
}
