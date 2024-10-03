package com.example.messageapp.controller;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.validation.Valid;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

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
public class UserController {
    private final UserService userService; // UserServiceインターフェースを使用
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final ProfileRepository profileRepository;
    private final ProfileService profileService;
    
    //まだどこに飛ばすか未定
    @GetMapping("/")
    public String top() {
        return "top";
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
    
    //ログイン画面表示
    @GetMapping("/login")
    public String showLogin() {
        return "login";
    }
    
//    //ユーザーリストボタンが押下されたときの処理
//    @GetMapping("/user-list")
//    public String userList(Model model, Principal principal) {
//    	
//    	// 自分のユーザー情報を取得
//        String username = principal.getName();     
//
//     // usersテーブルからすべてのユーザーを取得し、自分を除外
//        List<User> users = userRepository.findAll().stream()
//            .filter(user -> !user.getUsername().equals(username)) // 自分を除外
//            .collect(Collectors.toList());
//        
//        model.addAttribute("users", users);
//        return "user-list"; // user-list.htmlというテンプレートにリストを渡す
//    }
    
    //@AuthenticationPrincipalはログイン中のユーザー情報の詳細を取得できる。Principalはユーザーネームだけ。
    @GetMapping("/user-list")
    public String userList(Model model, @AuthenticationPrincipal UserDetails currentUser) {
        // 現在のログインユーザーの情報を取得
        String loggedInUsername = currentUser.getUsername();

        // usersテーブルからすべてのユーザーを取得し、自分を除外
        List<User> users = userRepository.findAll().stream() //取得したリストをストリームに変換して1件ずつ処理
            .filter(user -> !user.getUsername().equals(loggedInUsername)) // 自分を除外
            .collect(Collectors.toList());
        
        // 各ユーザーに対してプロフィール情報を取得し、ユーザーオブジェクトにセット
        users.forEach(user -> {
            Profile profile = profileRepository.findByUserId(user.getId());
            user.setProfile(profile); // ユーザーにプロフィール情報を設定
        });

        model.addAttribute("users", users);
        return "user-list"; // user-list.htmlというテンプレートにリストを渡す
    }


}
