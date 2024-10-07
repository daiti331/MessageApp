package com.example.messageapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.messageapp.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // ユーザー名でユーザーを検索
    User findByUsername(String username);
    
    //仮登録中のユーザーを全て取得
    List<User> findAllByEnabledFalse(); // enabledがfalseのユーザーを取得
    
//    List<User> findAllByEnabledTrue(); // enabledがtrueのユーザーだけを取得する（使ってない）
    
    // クエリでenabledがtrueかつ指定されたユーザー以外を取得
    @Query("SELECT u FROM User u WHERE u.enabled = true AND u.username <> :username")
    List<User> findAllByEnabledTrueAndUsernameNot(@Param("username") String username);
}
