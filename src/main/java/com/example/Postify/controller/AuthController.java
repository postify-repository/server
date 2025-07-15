package com.example.Postify.controller;

import com.example.Postify.dto.*;
import com.example.Postify.exception.BadRequestException;
import com.example.Postify.jwt.JwtUtil;
import com.example.Postify.security.CustomUserDetails;
import com.example.Postify.service.AuthService;
import com.example.Postify.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthService authService;
    private JwtUtil jwtUtil;

    @PostMapping("/signup")
    public ResponseEntity<UserSignupResponse> signup(@Valid @RequestBody UserSignupRequest request) {
        userService.signup(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new UserSignupResponse("회원가입이 완료되었습니다."));
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginSuccessResponse> login(@RequestBody UserLoginRequest request,
                                                          HttpServletResponse response) {
        UserLoginSuccessResponse loginResult = authService.login(request);

        // Refresh Token 생성
        String refreshToken = jwtUtil.generateRefreshToken(loginResult.getEmail());

        // 쿠키로 저장
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // https 환경일 경우
        cookie.setPath("/");
        cookie.setMaxAge(14 * 24 * 60 * 60); // 14일

        response.addCookie(cookie);

        return ResponseEntity.ok(loginResult);
    }


    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal CustomUserDetails userDetails,
                                       HttpServletResponse response) {
        // 서버에서 refreshToken 무효화할 경우 authService.logout() 실행
        authService.logout(userDetails.getUser().getEmail());

        // 쿠키 삭제
        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // 삭제

        response.addCookie(cookie);

        return ResponseEntity.ok().build();
    }


    // 회원 탈퇴
    @DeleteMapping("/account")
    public ResponseEntity<MessageResponse> deleteAccount(@AuthenticationPrincipal String email,
                                                         @RequestBody DeleteUserRequest request) {
        userService.deleteUser(email, request.getPassword());
        return ResponseEntity.ok(new MessageResponse("계정이 삭제되었습니다."));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<TokenRefreshResponse> refreshToken(@RequestBody TokenRefreshRequest request) {
        String newAccessToken = authService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(new TokenRefreshResponse(newAccessToken));
    }


}