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
import org.springframework.web.bind.annotation.RequestParam;

import com.example.messageapp.entity.Message;
import com.example.messageapp.entity.Profile;
import com.example.messageapp.entity.User;
import com.example.messageapp.form.UserRegisterForm;
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

    @GetMapping("/")
    public String top() {
        return "top";
    }
    
    // サインアップページを表示
    @GetMapping("/signup")
    public String showSignupForm(Model model) {
//        model.addAttribute("user", new User()); たぶんいらない
        model.addAttribute("userRegisterForm", new UserRegisterForm());
        return "signup"; // signup.htmlを返す
    }
    
    @PostMapping("/signup")
    public String signup(@Valid @ModelAttribute("userRegisterForm") UserRegisterForm userRegisterForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "signup"; // エラーがあれば再度signupページを表示
        }
        
		User user = new User();
		user.setUsername(userRegisterForm.getUsername());
        // パスワードをハッシュ化
        user.setPassword(userRegisterForm.getPassword());
		user.setEmail(userRegisterForm.getEmail());
		user.setRole("user");
		try {
		//ユーザーを作成
		userService.registerUser(user);
		} catch (Exception e) {
			System.err.println("ユーザーの作成に失敗しました: " + e.getMessage());
		}
		
	    // プロフィールを作成
	    profileService.createProfile(user); // ユーザーを引数として渡す
        
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
    
//    @GetMapping("/profile")
//    public String profile() {
//        return "profile";
//    }
    
    //まだ機能してない（多分プロフィールが一件もないから）
    @GetMapping("/profile/{userId}")
    public String profile(@PathVariable Long userId, Model model) {
        Profile profile = profileRepository.findByUserId(userId);
        model.addAttribute("profile", profile);
        return "profile";
    }
    
    // ログイン中のユーザーのプロフィールを表示するエンドポイント
    @GetMapping("/my-profile")
    public String myProfile(Model model, Principal principal) {
        // ログイン中のユーザー情報を取得
        String username = principal.getName();
        User user = userRepository.findByUsername(username);

        // プロフィール情報を取得
        Profile profile = profileRepository.findByUserId(user.getId());

        // プロフィール情報をモデルに追加
        model.addAttribute("profile", profile);
        return "myprofile";
    }
    
    @GetMapping("/myprofile-edit")
    public String showEditProfileForm(Model model, Principal principal) {
//        // 現在のユーザーのプロフィール情報を取得
//        UserProfile userProfile = userService.getCurrentUserProfile();
//        model.addAttribute("userProfile", userProfile);
//        return "myprofile-edit"; // myprofile-edit.htmlを表示
        // ログイン中のユーザー情報を取得
        String username = principal.getName();
        User user = userRepository.findByUsername(username);

        // プロフィール情報を取得
        Profile profile = profileRepository.findByUserId(user.getId());

        // プロフィール情報をモデルに追加
        model.addAttribute("profile", profile);
        return "myprofile-edit";
    }
    
    @PostMapping("/myprofile-edit")
    public String editProfile(@ModelAttribute("profile") Profile profile, Principal principal) {
    	// ログイン中のユーザー情報を取得
    	String username = principal.getName();
        User user = userRepository.findByUsername(username);
        profile.setUser(user);
        // プロフィールの更新処理
    	profileService.updateProfile(profile);
    	
        return "redirect:/my-profile"; // 更新後はマイプロフィールページにリダイレクト
    }
    
    @PostMapping("/profile-update")
    public String profileUpdate() {
        return "profile";
    }
    
//    @PostMapping("/message")
//    public String message() {
//        return "message";
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
        
        // 新規メッセージの数を計算（開始）
        long unreadCount = receivedMessages.stream().filter(message -> !message.isReadflag()).count();
        model.addAttribute("unreadCount", unreadCount);
     // 新規メッセージの数を計算（終了）
        
        model.addAttribute("receivedMessages", receivedMessages);

        // 送信メッセージを取得
        List<Message> sentMessages = messageRepository.findBySenderAndStatus(currentUser, "sent");
        model.addAttribute("sentMessages", sentMessages);

        return "mailbox";  // メールボックスのHTMLを返す
    }
    
    @GetMapping("/message/received/{id}")
    public String viewMessage(@PathVariable Long id, Model model) {
        Message message = messageRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid message Id:" + id));            
        
        // メッセージを既読にする
        message.setReadflag(true);
        messageRepository.save(message); // 更新を保存
        
        //message.hmtlで使うための情報を詰める
        model.addAttribute("message", message);
        
        // message.htmlを返す
        return "message"; 
    }
    
    @GetMapping("/message/sent/{id}")
    public String viewSentMessage(@PathVariable Long id, Model model) {
        Message message = messageRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid message Id:" + id));            
        
        // 送信箱からの閲覧なので、readflagを変更しない
        
        // message.htmlで使うための情報を詰める
        model.addAttribute("message", message);
        
        // message.htmlを返す
        return "message"; 
    }
}
