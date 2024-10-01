package com.example.messageapp.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.messageapp.entity.Profile;
import com.example.messageapp.entity.User;
import com.example.messageapp.repository.MessageRepository;
import com.example.messageapp.repository.ProfileRepository;
import com.example.messageapp.repository.UserRepository;
import com.example.messageapp.servicce.ProfileService;
import com.example.messageapp.servicce.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ProfileController {
    private final UserService userService; // UserServiceインターフェースを使用
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final ProfileRepository profileRepository;
    private final ProfileService profileService;
    
    //ユーザーリストから押下したユーザーのプロフィールを表示
    @GetMapping("/profile/{userId}")
    public String profile(@PathVariable Long userId, Model model) {
        Profile profile = profileRepository.findByUserId(userId);
        model.addAttribute("profile", profile);
        return "profile";
    }
    
    // 自分のプロフィールを表示する
    @GetMapping("/my-profile")
    public String myProfile(Model model, Principal principal) {
        // 自分のユーザー情報を取得
        String username = principal.getName();
        User user = userRepository.findByUsername(username);

        // プロフィール情報を取得
        Profile profile = profileRepository.findByUserId(user.getId());

        // プロフィール情報をモデルに追加
        model.addAttribute("profile", profile);
        return "myprofile";
    }
    
    //プロフィール画面からプロフィールを編集ボタンを押したときの処理
    @GetMapping("/myprofile-edit")
    public String showEditProfileForm(Model model, Principal principal) {
        // ログイン中のユーザー情報を取得
        String username = principal.getName();
        User user = userRepository.findByUsername(username);

        // プロフィール情報を取得
        Profile profile = profileRepository.findByUserId(user.getId());

        // プロフィール情報をモデルに追加
        model.addAttribute("profile", profile);
        return "myprofile-edit";
    }
    
    //プロフィール編集画面から保存ボタンを押下したときの処理
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
    
//    @PostMapping("/profile-update")
//    public String profileUpdate() {
//        return "profile";
//    }

}
