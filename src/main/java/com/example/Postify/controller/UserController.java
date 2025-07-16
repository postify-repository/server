package com.example.Postify.controller;

import com.example.Postify.domain.User;
import com.example.Postify.dto.UserMeResponse;
import com.example.Postify.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserMeResponse> getMyInfo(Authentication authentication) {
        String email = authentication.getName(); // 토큰에서 subject(email)
        User user = userService.findByEmail(email);

        UserMeResponse response = UserMeResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .displayName(user.getDisplayName() != null ? user.getDisplayName() : "")
                .profileImage(user.getProfileImage() != null ? user.getProfileImage() : "")
                .shortBio(user.getShortBio() != null ? user.getShortBio() : "")
                .build();

        return ResponseEntity.ok(response);
    }
}
