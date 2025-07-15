package com.example.Postify.controller;

import com.example.Postify.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    @GetMapping("/me")
    public ResponseEntity<?> getMe(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(userDetails.getUser());
    }

}
