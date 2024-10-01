package com.example.messageapp.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.messageapp.entity.Message;
import com.example.messageapp.entity.User;
import com.example.messageapp.repository.MessageRepository;
import com.example.messageapp.repository.ProfileRepository;
import com.example.messageapp.repository.UserRepository;
import com.example.messageapp.servicce.ProfileService;
import com.example.messageapp.servicce.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MessageController {
	
    private final UserService userService; // UserServiceインターフェースを使用
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final ProfileRepository profileRepository;
    private final ProfileService profileService;
    
    //メールボックスの初期表示
    @GetMapping("/message/box")
    public String showmessagebox(Model model, Principal principal) {
        User currentUser = userRepository.findByUsername(principal.getName());
        
        

        // 受信メッセージを取得
        List<Message> receivedMessages = messageRepository.findByRecipientAndStatus(currentUser, "sent");
        
        // 新規メッセージの数を計算（開始）
        long unreadCount = receivedMessages.stream().filter(message -> !message.isReadflag()).count();
        model.addAttribute("unreadCount", unreadCount);
     // 新規メッセージの数を計算（終了）
        
        model.addAttribute("receivedMessages", receivedMessages);

        // 送信メッセージを取得
        List<Message> sentMessages = messageRepository.findBySenderAndStatus(currentUser, "sent");
        model.addAttribute("sentMessages", sentMessages);

        return "message-box";  // メールボックスのHTMLを返す
    }
    
    //ユーザーリストから「メッセージ送信」ボタンを押下したときの処理
    @GetMapping("/message/send/{id}")
    public String sendMessage(@PathVariable("id") Long userId, Model model) {
        // ユーザーIDに基づいて送信先のユーザーを取得
        User recipient = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + userId));
        model.addAttribute("recipient", recipient);
        return "send-message"; // メッセージ送信用のテンプレート
    }
    
    //send-messageから「送信」ボタンを押下したときの処理
    @PostMapping("/message/send")
    public String sendMessage(
        @RequestParam("recipientId") Long recipientId,
        @RequestParam("content") String content,
        Principal principal) {
    	
    	// senderがnullでないか確認
        if (principal == null) {
            System.out.println("Sender is null. User is not authenticated.");
            return "redirect:/login"; // 認証されていない場合はログインページにリダイレクト
        }
    	
    	// Principalからユーザー名を取得
        String username = principal.getName();
        
     // ユーザー名を使ってデータベースからUserオブジェクトを取得
        User sender = userRepository.findByUsername(username);
        
        //User sender = userRepository.findByUsername(principal.getName());
        // 送信先のユーザーを取得
        User recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new RuntimeException("Recipient not found"));
        
        Message message = new Message();
        message.setSender(sender);
        message.setRecipient(recipient);
        message.setContent(content);
        message.setTimestamp(LocalDateTime.now());
        message.setStatus("sent");
        
        System.out.println("Sending message to: " + recipient.getId());
        System.out.println("Message content: " + content);
        System.out.println("Sending message to: " + sender.getId());
        System.out.println("Sending message to: " + message.getTimestamp());
        System.out.println("Message content: " + message.getStatus());
        
        messageRepository.save(message);

        return "redirect:/message/box";  // メールボックスにリダイレクト
    }
    
    //メールボックスの送信箱から特定のメールを開いたときの処理
    @GetMapping("/message/sent/{id}")
    public String viewSentMessage(@PathVariable Long id, Model model) {
        Message message = messageRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid message Id:" + id));            
        
        // 送信箱からの閲覧なので、readflagを変更しない
        
        // message.htmlで使うための情報を詰める
        model.addAttribute("message", message);
        
        // 送信メッセージかどうかのフラグを追加（返信ぼたん非表示のため）
        model.addAttribute("isSentMessage", true);
        
        // message.htmlを返す
        return "message"; 
    }
    
    //メールボックスの受信箱から特定のメールを開いたときの処理
    @GetMapping("/message/received/{id}")
    public String viewMessage(@PathVariable Long id, Model model) {
        Message message = messageRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid message Id:" + id));            
        
        // メッセージを既読にする
        message.setReadflag(true);
        messageRepository.save(message); // 更新を保存
        
        //message.hmtlで使うための情報を詰める
        model.addAttribute("message", message);
        
        // 受信メッセージかどうかのフラグを追加（返信ぼたん表示のため）
        model.addAttribute("isSentMessage", false);
        
        // message.htmlを返す
        return "message"; 
    }
    
    //受信箱からメッセージを開いて、返信ボタンを押下したときの処理
    @PostMapping("/message/reply")
    public String replyToMessage(@RequestParam Long recipientId, @RequestParam String content, Principal principal) {
        // 返信メッセージを作成
        Message replyMessage = new Message();
        replyMessage.setContent(content);
        
        // 送信者と受信者の設定
        String username = principal.getName();
        User sender = userRepository.findByUsername(username);// 現在のユーザーを取得（認証機能に応じて）
        User recipient = userRepository.findById(recipientId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid recipient Id:" + recipientId));
        replyMessage.setSender(sender);
        replyMessage.setRecipient(recipient);
        
        // タイムスタンプやステータスの設定（必要に応じて）
        replyMessage.setTimestamp(LocalDateTime.now());
        replyMessage.setStatus("sent"); // または適切なステータス

        // メッセージを保存
        messageRepository.save(replyMessage);

        // メールボックスにリダイレクト
        return "redirect:/message/box";
    }
}
