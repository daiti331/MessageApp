package com.example.messageapp.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.messageapp.entity.Message;
import com.example.messageapp.entity.User;
import com.example.messageapp.form.MessageForm;
import com.example.messageapp.service.MessageService;
import com.example.messageapp.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MessageController {
	
    private final UserService userService; // UserServiceインターフェースを使用
    private final MessageService messageService;  // Serviceを注入
    
    // 共通処理：未読メッセージのカウントをモデルに追加
    @ModelAttribute
    public void addUnreadCountToModel(Model model, Principal principal) {
        long unreadCount = messageService.getUnreadCountForUser(principal);
        model.addAttribute("unreadCount", unreadCount);
    }
    
    //メールボックスの初期表示
    @GetMapping("/message/box")
    public String showmessagebox(Model model, Principal principal) {
    	
        // UserRepositoryではなく、UserServiceを利用
        User currentUser = userService.findByUsername(principal.getName());

        // 受信メッセージを取得
        List<Message> receivedMessages = messageService.getReceivedMessages(currentUser);
        model.addAttribute("receivedMessages", receivedMessages);

        // 新規メッセージの数を計算
        long unreadCount = messageService.getUnreadCount(currentUser);
        model.addAttribute("unreadCount", unreadCount);

        // 送信メッセージを取得
        List<Message> sentMessages = messageService.getSentMessages(currentUser);
        model.addAttribute("sentMessages", sentMessages);

        return "message-box";  // メールボックスのHTMLを返す
    }
    
    //ユーザーリストから「メッセージ送信」ボタンを押下したときの処理
    @GetMapping("/message/send/{id}")
    //変更開始
//    public String sendMessage(@PathVariable("id") Long userId, Model model) {
    public String showSendMessageForm(@PathVariable("id") Long userId, Model model) {
    //変更終了

        // ユーザーIDに基づいて送信先のユーザーを取得
        User recipient = userService.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + userId));
    	model.addAttribute("recipient", recipient);
    	//変更開始
        // フォームを初期化
        MessageForm messageForm = new MessageForm();
        messageForm.setRecipientId(recipient.getId());
        
        // モデルにフォームと送信先ユーザーを追加
        model.addAttribute("messageForm", messageForm);
        model.addAttribute("recipient", recipient);
        //変更終了
        
        return "send-message"; // メッセージ送信用のテンプレート
    }
    
    //send-messageから「送信」ボタンを押下したときの処理
    @PostMapping("/message/send")
    //変更開始
//    public String sendMessage(
//        @RequestParam("recipientId") Long recipientId,
//        @RequestParam("content") String content,
//        Principal principal) {
    public String sendMessage(@ModelAttribute("messageForm") @Valid MessageForm messageForm, BindingResult result, Principal principal, Model model) {
    	//変更終了
    	// 認証されたユーザーがいない場合はログインページにリダイレクト
        if (principal == null) {
            System.out.println("Sender is null. User is not authenticated.");
            return "redirect:/login"; // 認証されていない場合はログインページにリダイレクト
        }
        
        //変更開始
        // フォームのバリデーション
        if (result.hasErrors()) {
            // エラーがあればフォームに戻る
            User recipient = userService.findById(messageForm.getRecipientId())
                    .orElseThrow(() -> new RuntimeException("Recipient not found"));
            model.addAttribute("recipient", recipient);
            return "send-message";
        }
        //変更終了
    	
    	// Principalからユーザー名を取得
        String username = principal.getName();
        
        // ユーザー名を使ってデータベースからUserオブジェクトを取得（UserServiceを利用）
        User sender = userService.findByUsername(username);
        
        // 送信先のユーザーを取得（UserServiceを利用）
        User recipient = userService.findById(messageForm.getRecipientId())
                .orElseThrow(() -> new RuntimeException("Recipient not found"));
        
        //変更開始
        // メッセージ送信処理をMessageServiceに委譲
        messageService.sendMessage(sender, recipient, messageForm.getContent());
      //変更終了

        return "redirect:/message/box";  // メールボックスにリダイレクト
    }
    
    //メールボックスの送信箱から特定のメールを開いたときの処理
    @GetMapping("/message/sent/{id}")
    public String viewSentMessage(@PathVariable Long id, Model model) {
        
        // メッセージIDに基づいてメッセージを取得
        Message message = messageService.findById(id)
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
        
        // メッセージIDに基づいてメッセージを取得
        Message message = messageService.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid message Id:" + id)); 
        
        // メッセージを既読にする
        messageService.markAsRead(message); // Serviceで既読フラグを更新
        
        //message.hmtlで使うための情報を詰める
        model.addAttribute("message", message);
        
        // 受信メッセージかどうかのフラグを追加（返信ぼたん表示のため）
        model.addAttribute("isSentMessage", false);
        
        // message.htmlを返す
        return "message"; 
    }
    
    //受信箱からメッセージを開いて、返信ボタンを押下したときの処理
    @PostMapping("/message/reply")
    //変更開始
//    public String replyToMessage(@RequestParam Long recipientId, @RequestParam String content, Principal principal) {
    public String replyToMessage(@ModelAttribute @Valid MessageForm messageForm, BindingResult bindingResult, Principal principal) {
    	//変更終了
        if (bindingResult.hasErrors()) {
            return "message"; // エラーがある場合は元のメッセージ詳細を再表示
        }
        // 返信メッセージを作成
        Message replyMessage = new Message();
        //変更開始
//        replyMessage.setContent(content);
        replyMessage.setContent(messageForm.getContent());
        //変更終了

        
        // 送信者と受信者の設定
        String username = principal.getName();
        User sender = userService.findByUsername(username); // UserServiceを利用
        //変更開始
//        User recipient = userService.findById(recipientId) // UserServiceを利用
//            .orElseThrow(() -> new IllegalArgumentException("Invalid recipient Id:" + recipientId));
        User recipient = userService.findById(messageForm.getRecipientId()) // UserServiceを利用
                .orElseThrow(() -> new IllegalArgumentException("Invalid recipient Id:" + messageForm.getRecipientId()));
        //変更終了
        replyMessage.setSender(sender);
        replyMessage.setRecipient(recipient);
        
        // タイムスタンプやステータスの設定（必要に応じて）
        replyMessage.setTimestamp(LocalDateTime.now());
        replyMessage.setStatus("sent"); // または適切なステータス
        
        // メッセージを保存
        messageService.saveMessage(replyMessage); // Serviceでメッセージを保存

        // メールボックスにリダイレクト
        return "redirect:/message/box";
    }
}
