//package com.example.messageapp.security;
//
//import java.util.Collection;
//import java.util.List;
//
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//
//import com.example.messageapp.entity.User;
//
//public class CustomUserDetails implements UserDetails {
//    private final User user;
//
//    public CustomUserDetails(User user) {
//        this.user = user;
//    }
//
//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        // ユーザーのロールを取得し、GrantedAuthorityとして返す
//        return List.of(() -> user.getRole());
//    }
//
//    @Override
//    public String getPassword() {
//        return user.getPassword();
//    }
//
//    @Override
//    public String getUsername() {
//        return user.getUsername();
//    }
//
//    @Override
//    public boolean isAccountNonExpired() {
//        return true; // アカウントが有効である場合はtrue
//    }
//
//    @Override
//    public boolean isAccountNonLocked() {
//        return true; // アカウントがロックされていない場合はtrue
//    }
//
//    @Override
//    public boolean isCredentialsNonExpired() {
//        return true; // 資格情報が有効である場合はtrue
//    }
//
//    @Override
//    public boolean isEnabled() {
//        return true; // アカウントが有効である場合はtrue
//    }
//
//    public User getUser() {
//        return user; // 元のUserオブジェクトを返す
//    }
//}
