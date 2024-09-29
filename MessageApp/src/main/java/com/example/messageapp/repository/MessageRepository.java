package com.example.messageapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.messageapp.entity.Message;
import com.example.messageapp.entity.User;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    // 受信メッセージを取得
    List<Message> findByRecipientAndStatus(User recipient, String status);
    
    // 送信メッセージを取得
    List<Message> findBySenderAndStatus(User sender, String status);
}