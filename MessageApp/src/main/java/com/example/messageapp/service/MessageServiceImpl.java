package com.example.messageapp.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.messageapp.entity.Message;
import com.example.messageapp.entity.User;
import com.example.messageapp.repository.MessageRepository;
import com.example.messageapp.repository.UserRepository;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<Message> getReceivedMessages(User user) {
        return messageRepository.findByRecipientAndStatus(user, "sent");
    }

    @Override
    public List<Message> getSentMessages(User user) {
        return messageRepository.findBySenderAndStatus(user, "sent");
    }

    @Override
    public long getUnreadCount(User user) {
        List<Message> receivedMessages = getReceivedMessages(user);
        return receivedMessages.stream().filter(message -> !message.isReadflag()).count();
    }
    
    @Override
    public void sendMessage(User sender, User recipient, String content) {
        Message message = new Message();
        message.setSender(sender);
        message.setRecipient(recipient);
        message.setContent(content);
        message.setTimestamp(LocalDateTime.now());
        message.setStatus("sent");
        
        messageRepository.save(message);
    }
    
    // メッセージIDに基づいてメッセージを取得するメソッドの実装
    @Override
    public Optional<Message> findById(Long id) {
        return messageRepository.findById(id);
    }
    
    @Override
    public void markAsRead(Message message) {
        message.setReadflag(true);
        messageRepository.save(message); // メッセージの更新を保存
    }
    
    @Override
    public void saveMessage(Message message) {
        messageRepository.save(message); // メッセージを保存
    }
}