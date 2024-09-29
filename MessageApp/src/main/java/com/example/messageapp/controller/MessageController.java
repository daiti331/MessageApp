package com.example.messageapp.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.messageapp.entity.Message;
import com.example.messageapp.entity.User;
import com.example.messageapp.form.UserRegisterForm;
import com.example.messageapp.repository.MessageRepository;
import com.example.messageapp.repository.UserRepository;
import com.example.messageapp.servicce.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MessageController {
	
    private final UserService userService; // UserServiceインターフェースを使用
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    @GetMapping("/")
    public String top() {
        return "top";
    }
    
    // サインアップページを表示
    @GetMapping("/signup")
    public String showSignupForm(Model model) {
        model.addAttribute("user", new User());
        return "signup"; // signup.htmlを返す
    }
    
    @PostMapping("/signup")
    public String signup(@Valid UserRegisterForm userRegisterForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "signup"; // エラーがあれば再度signupページを表示
        }
        
		User user = new User();
		user.setUsername(userRegisterForm.getUsername());
        // パスワードをハッシュ化
        user.setPassword(userRegisterForm.getPassword());
		user.setEmail(userRegisterForm.getEmail());
		user.setRole("viewer");
		userService.registerUser(user);
        
        return "redirect:/login"; // 登録後はログインページへリダイレクト
    }
    
    @GetMapping("/login")
    public String showLogin() {
        return "login";
    }
    
//    @PostMapping("/login")
//    public String login() {
//        return "top";
//    }
    
    @GetMapping("/user-list")
    public String userList(Model model) {
        // usersテーブルからすべてのユーザーを取得
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "user-list"; // user-list.htmlというテンプレートにリストを渡す
    }
    
    @GetMapping("/profile")
    public String profile() {
        return "profile";
    }
    
    @GetMapping("/profile-edit")
    public String profileEdit() {
        return "profile-edit";
    }
    
    @PostMapping("/profile-update")
    public String profileUpdate() {
        return "profile";
    }
    
    @PostMapping("/message")
    public String message() {
        return "message";
    }
    
//    @GetMapping("/message-input")
//    public String messageInput() {
//        return "message-input";
//    }
    
//    @PostMapping("/message-send")
//    public String messageSend() {
//        return "user-list";
//    }
    
    @GetMapping("/message/send/{id}")
    public String sendMessage(@PathVariable("id") Long userId, Model model) {
        // ユーザーIDに基づいて送信先のユーザーを取得
        User recipient = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + userId));
        model.addAttribute("recipient", recipient);
        return "send-message"; // メッセージ送信用のテンプレート
    }
    
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

        return "redirect:/mailbox";  // メールボックスにリダイレクト
    }
    
    @GetMapping("/mailbox")
    public String showMailbox(Model model, Principal principal) {
        User currentUser = userRepository.findByUsername(principal.getName());

        // 受信メッセージを取得
        List<Message> receivedMessages = messageRepository.findByRecipientAndStatus(currentUser, "sent");
        model.addAttribute("receivedMessages", receivedMessages);

        // 送信メッセージを取得
        List<Message> sentMessages = messageRepository.findBySenderAndStatus(currentUser, "sent");
        model.addAttribute("sentMessages", sentMessages);

        return "mailbox";  // メールボックスのHTMLを返す
    }
    
//    @GetMapping("/mailbox")
//    public String mailbox() {
//        return "mailbox";
//    }

//    @PostMapping("/send")
//    public String sendMessage(@ModelAttribute Message message) {
//        messageService.sendMessage(message);
//        return "redirect:/messages?recipient=" + message.getRecipient();
//    }
//
//    // REST API example for sending messages
//    @PostMapping("/api/messages")
//    @ResponseBody
//    public void sendMessageAPI(@RequestBody Message message) {
//        messageService.sendMessage(message);
//    }
//
//    @GetMapping("/api/messages/{recipient}")
//    @ResponseBody
//    public List<Message> getMessagesAPI(@PathVariable String recipient) {
//        return messageService.getMessages(recipient);
//    }
}
