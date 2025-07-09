package com.example.Postify.controller;


import com.example.Postify.dto.DeleteUserRequest;
import com.example.Postify.dto.UserLoginRequest;
import com.example.Postify.dto.UserSignupRequest;
import com.example.Postify.security.UserDetailsImpl;
import com.example.Postify.service.AuthService;
import com.example.Postify.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody UserSignupRequest request) {
        userService.signup(request);
        return ResponseEntity.status(201).body("회원가입 성공");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginRequest request) {
        String token = authService.login(request);
        return ResponseEntity.ok().body("Bearer " + token);
    }

    @DeleteMapping("/users/me")
    public ResponseEntity<?> deleteUser(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody DeleteUserRequest request
    ) {
        userService.deleteUser(userDetails.getUser(), request.getPassword());
        return ResponseEntity.ok("회원 탈퇴 완료");
    }

}