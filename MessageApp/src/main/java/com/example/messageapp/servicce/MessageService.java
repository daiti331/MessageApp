package com.example.messageapp.servicce;

import java.util.List;

import com.example.messageapp.entity.Message;
import com.example.messageapp.entity.User;

public interface MessageService {
    List<Message> getReceivedMessages(User user);
    List<Message> getSentMessages(User user);
    long getUnreadCount(User user);
    void sendMessage(User sender, User recipient, String content);
}