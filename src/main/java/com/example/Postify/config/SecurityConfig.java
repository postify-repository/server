package com.example.Postify.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // Spring Boot 기본 보안 설정을 없애줌
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // CSRF 비활성화
                .formLogin(form -> form.disable()) // 폼 로그인 X
                .httpBasic(basic -> basic.disable()) // Basic 인증 X
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll() // 회원가입 요청 허용
                        .anyRequest().permitAll()  // 나머지도 전부 허용 ← 실무에서는 여긴 authenticated() 쓰지만 지금은 개발단계
                );
        return http.build();

    }
}