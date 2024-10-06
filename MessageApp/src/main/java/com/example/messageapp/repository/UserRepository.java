package com.example.messageapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.messageapp.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // ユーザー名でユーザーを検索
    User findByUsername(String username);
    
    //仮登録中のユーザーを全て取得
    List<User> findAllByEnabledFalse(); // enabledがfalseのユーザーを取得
}
