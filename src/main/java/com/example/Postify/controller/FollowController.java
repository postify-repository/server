package com.example.Postify.controller;

import com.example.Postify.dto.FollowResponse;
import com.example.Postify.exception.BadRequestException;
import com.example.Postify.exception.ConflictException;
import com.example.Postify.exception.UserNotFoundException;
import com.example.Postify.security.CustomUserDetails;
import com.example.Postify.service.FollowService;
import com.example.Postify.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class FollowController {

    private final FollowService followService;
    private final UserService userService;

    @PostMapping("/follow/{userId}")
    public ResponseEntity<FollowResponse> followUser(
            @PathVariable Long userId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        String email = userDetails.getUsername();
        FollowResponse response = followService.followUser(email, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/follow/{userId}")
    public ResponseEntity<FollowResponse> unfollowUser(
            @PathVariable Long userId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        String email = userDetails.getUsername();
        FollowResponse response = followService.unfollowUser(email, userId);
        return ResponseEntity.ok(response); // 200 OK
    }

}
