package com.example.messageapp.controller;

import java.security.Principal;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.messageapp.entity.User;
import com.example.messageapp.form.UserRegisterForm;
import com.example.messageapp.service.MessageService;
import com.example.messageapp.service.ProfileService;
import com.example.messageapp.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService; // UserServiceインターフェースを使用
    private final ProfileService profileService;
    private final MessageService messageService;
    
//    //変更開始
//    private final VerificationTokenRepository verificationTokenRepository;
//    private final UserRepository userRepository;
//    private final EmailService emailService;
//    //変更終了
    
    // 共通処理：未読メッセージのカウントをモデルに追加
    @ModelAttribute
    public void addUnreadCountToModel(Model model, Principal principal) {
        long unreadCount = messageService.getUnreadCountForUser(principal);
        model.addAttribute("unreadCount", unreadCount);
    }
    
    //まだどこに飛ばすか未定
    @GetMapping("/")
    public String top() {
        return "top";
    }
    
//    //変更開始
//    @GetMapping("/confirm")
//    public String confirmEmail(@RequestParam("token") String token) {
//        VerificationToken verificationToken = verificationTokenRepository.findByToken(token);
//        if (verificationToken == null) {
//            // トークンが無効な場合の処理
//            return "redirect:/signup?error"; // エラー画面にリダイレクト
//        }
//        
//        User user = verificationToken.getUser();
//        user.setEnabled(true); // ユーザーのアカウントを有効化
//        userRepository.save(user); // ユーザー情報を保存
//        
//        return "redirect:/login"; // 確認後はログインページへリダイレクト
//    }
//    //変更終了
    
    // サインアップページを表示
    @GetMapping("/signup")
    public String showSignupForm(Model model) {
        model.addAttribute("userRegisterForm", new UserRegisterForm());
        return "signup"; // signup.htmlを返す
    }
    
    //サインアップボタンが押下されたときの処理
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
		//ユーザーを作成
		userService.registerUser(user);
		
//		//変更開始
//	    // 確認トークンを生成（UUIDなどを利用）
//	    String token = UUID.randomUUID().toString();
//	    userService.createVerificationToken(user, token);
//
//	    // 確認メールを送信
//	    emailService.sendConfirmationEmail(user.getEmail(), token);
//	  //変更終了

	    // プロフィールを作成
	    profileService.createProfile(user); // ユーザーを引数として渡す
        
        return "redirect:/login"; // 登録後はログインページへリダイレクト
    }
    
    //ログイン画面表示
    @GetMapping("/login")
    public String showLogin() {
        return "login";
    }
    
    //@AuthenticationPrincipalはログイン中のユーザー情報の詳細を取得できる。Principalはユーザーネームだけ。
    @GetMapping("/user-list")
    public String userList(Model model, @AuthenticationPrincipal UserDetails currentUser) {
        // 現在のログインユーザーの情報を取得
        String loggedInUsername = currentUser.getUsername();
        
        // UserServiceを利用して、全ユーザーを取得し、自分を除外
        List<User> users = userService.findAllUsersExcept(loggedInUsername);
        
        // プロフィール情報を設定する
        userService.populateUserProfiles(users);

        model.addAttribute("users", users);
        return "user-list"; // user-list.htmlというテンプレートにリストを渡す
    }


}
