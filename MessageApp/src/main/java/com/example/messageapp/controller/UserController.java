package com.example.messageapp.controller;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.messageapp.entity.User;
import com.example.messageapp.entity.VerificationToken;
import com.example.messageapp.form.LoginForm;
import com.example.messageapp.form.UserRegisterForm;
import com.example.messageapp.service.EmailService;
import com.example.messageapp.service.MessageService;
import com.example.messageapp.service.ProfileService;
import com.example.messageapp.service.UserService;
import com.example.messageapp.service.VerificationService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService; // UserServiceインターフェースを使用
    private final ProfileService profileService;
    private final MessageService messageService;
    private final EmailService emailService;
    private final VerificationService verificationService;
    
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
    
    @GetMapping("/confirm")
    public String confirmEmail(@RequestParam("token") String token) {
//        VerificationToken verificationToken = verificationTokenRepository.findByToken(token);
        VerificationToken verificationToken = verificationService.findByToken(token);
        if (verificationToken == null) {
            // トークンが無効な場合の処理
            return "redirect:/signup?error"; // エラー画面にリダイレクト
        }
        // トークンが期限切れかどうかを確認
        if (verificationToken.isExpired()) {
            // トークンが期限切れの場合の処理
            return "redirect:/signup?error=tokenExpired"; // 期限切れエラーページにリダイレクト
        }

        // ユーザーアカウントを有効化
        verificationService.enableUserAccount(verificationToken);
        
        return "redirect:/login"; // 確認後はログインページへリダイレクト
    }
    
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
		user.setEnabled(false); // サインアップ時は有効化されていない（仮登録状態）
		//ユーザーを作成
		userService.registerUser(user);
	    // プロフィールを作成
	    profileService.createProfile(user); // ユーザーを引数として渡す
		
	    // 確認トークンを生成（UUIDなどを利用）
	    String token = UUID.randomUUID().toString();
	    userService.createVerificationToken(user, token);

	    // 確認メールを送信
	    emailService.sendConfirmationEmail(user.getEmail(), token);
	    
        return "redirect:/login"; // 登録後はログインページへリダイレクト
    }
    
    //ログイン画面表示
    @GetMapping("/login")
    public String showLoginForm(@RequestParam(value = "error", required = false) String error, Model model) {
    	if (error != null) {
            model.addAttribute("errorMessage", "ユーザー名またはパスワードが正しくありません");
        }
    	model.addAttribute("loginForm", new LoginForm());
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(@Valid @ModelAttribute("loginForm") LoginForm loginForm, BindingResult result) {
        if (result.hasErrors()) {
            return "login"; // エラーがある場合はログインページに戻る
        }
        // 認証処理 (実際のログイン処理はSpring Securityで行われる)
        return "redirect:/user-list"; // 成功した場合、ユーザー一覧へリダイレクト
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
