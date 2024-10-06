
package com.example.messageapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.example.messageapp.entity.User;
import com.example.messageapp.repository.UserRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserRepository userRepository;

    public SecurityConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // ユーザーの認証情報をJPAを使用して取得する
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            User user = userRepository.findByUsername(username);
            if (user == null) {
                throw new UsernameNotFoundException("User not found");
         
            }
            System.out.println(user.getUsername());
            System.out.println(user.getPassword());
            System.out.println(user.getRole());
            // Spring SecurityのUserDetailsを使用してユーザーを返す
            return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole())
                .build();
        };
    }

    // パスワードの暗号化
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // セキュリティフィルターチェーンの設定
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests((requests) -> requests
                .requestMatchers("/login", "/signup").permitAll() // ログイン画面とサインアップ画面は誰でもアクセス可能
                .anyRequest().authenticated() // それ以外のページは認証が必要
            )
            .formLogin((form) -> form
            	.loginProcessingUrl("/login") // ログイン処理のパス
                .loginPage("/login") // ログインページのURL
//                .failureUrl("/login?error") // ログイン失敗時の遷移先
                .usernameParameter("username") // ログインページのユーザーID
                .passwordParameter("password") // ログインページのパスワード
                .defaultSuccessUrl("/user-list", true) // ログイン成功後にリダイレクトするページ
                .permitAll()
            )
            .logout((logout) -> logout
                    .logoutUrl("/logout") // ログアウトのURL
                    .logoutSuccessUrl("/login?logout") // ログアウト後にリダイレクトするURL
                    .invalidateHttpSession(true) // セッションの無効化
                    .deleteCookies("JSESSIONID") // クッキーの削除
                    .permitAll()
                );

        return http.build();
    }
}
