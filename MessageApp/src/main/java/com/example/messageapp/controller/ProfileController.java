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
import com.example.messageapp.form.ProfileForm;
import com.example.messageapp.service.MessageService;
import com.example.messageapp.service.ProfileService;
import com.example.messageapp.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ProfileController {
    private final UserService userService; // UserServiceインターフェースを使用
    private final ProfileService profileService;
    private final MessageService messageService;
    
    // 共通処理：未読メッセージのカウントをモデルに追加
    @ModelAttribute
    public void addUnreadCountToModel(Model model, Principal principal) {
        long unreadCount = messageService.getUnreadCountForUser(principal);
        model.addAttribute("unreadCount", unreadCount);
    }
    
    //ユーザーリストから押下したユーザーのプロフィールを表示
    @GetMapping("/profile/{userId}")
    public String profile(@PathVariable Long userId, Model model) {
        Profile profile = profileService.findByUserId(userId); // ProfileServiceを利用
        model.addAttribute("profile", profile);
        return "profile";
    }
    
    // 自分のプロフィールを表示する
    @GetMapping("/my-profile")
    public String myProfile(Model model, Principal principal) {
        // 自分のユーザー情報を取得
        String username = principal.getName();
        User user = userService.findByUsername(username); // UserServiceを利用してユーザー情報を取得
        
        // プロフィール情報を取得
        Profile profile = profileService.findByUserId(user.getId()); // ProfileServiceを利用してプロフィール情報を取得

        // プロフィール情報をモデルに追加
        model.addAttribute("profile", profile);
        return "myprofile";
    }
    
    //プロフィール画面からプロフィールを編集ボタンを押したときの処理
    @GetMapping("/myprofile-edit")
    public String showEditProfileForm(Model model, Principal principal) {
        // ログイン中のユーザー情報を取得
        String username = principal.getName();
        User user = userService.findByUsername(username); // UserServiceを利用してユーザー情報を取得
        
        // プロフィール情報を取得
        Profile profile = profileService.findByUserId(user.getId()); // ProfileServiceを利用してプロフィール情報を取得
        
        // フォームオブジェクトを初期化し、プロフィール情報を設定
        ProfileForm profileForm = new ProfileForm();
        profileForm.setId(profile.getId()); // プロフィールのIDを設定
        profileForm.setDisplayName(profile.getDisplayName());
        profileForm.setAge(profile.getAge());
        profileForm.setGender(profile.getGender());
        profileForm.setLocation(profile.getLocation());
        profileForm.setBio(profile.getBio());
        
        model.addAttribute("profileForm", profileForm); // モデルにprofileFormを追加

        return "myprofile-edit";
    }
    
//    //プロフィール編集画面から保存ボタンを押下したときの処理
//    @PostMapping("/myprofile-edit")
//    public String editProfile(@ModelAttribute("profile") Profile profile, Principal principal) {
//    	// ログイン中のユーザー情報を取得
//    	String username = principal.getName();
//        User user = userService.findByUsername(username); // UserServiceを利用してユーザー情報を取得
//        profile.setUser(user); // プロフィールにユーザー情報を設定
//        
//        // プロフィールの更新処理
//    	profileService.updateProfile(profile);
//    	
//        return "redirect:/my-profile"; // 更新後はマイプロフィールページにリダイレクト
//    }
    @PostMapping("/myprofile-edit")
    public String editProfile(@ModelAttribute("profileForm") ProfileForm profileForm, Principal principal) {
        // ログイン中のユーザー情報を取得
        String username = principal.getName();
        User user = userService.findByUsername(username); // UserServiceを利用してユーザー情報を取得
        
        // プロフィールの更新処理
        Profile profile = new Profile();
        profile.setUser(user);
        profile.setId(profileForm.getId());
        profile.setDisplayName(profileForm.getDisplayName());
        profile.setAge(profileForm.getAge());
        profile.setGender(profileForm.getGender());
        profile.setLocation(profileForm.getLocation());
        profile.setBio(profileForm.getBio());
        
        profileService.updateProfile(profile);
        
        return "redirect:/my-profile"; // 更新後はマイプロフィールページにリダイレクト
    }

}
