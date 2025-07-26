package com.example.Postify.controller;

import com.example.Postify.domain.User;
import com.example.Postify.dto.*;
import com.example.Postify.exception.BadRequestException;
import com.example.Postify.exception.UnauthorizedException;
import com.example.Postify.service.UserService;
import com.example.Postify.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final PostService postService;

    @GetMapping("/me")
    public ResponseEntity<UserMeResponse> getMyInfo(Authentication authentication) {
        String email = authentication.getName(); // 토큰에서 subject(email)
        User user = userService.findByEmail(email);

        UserMeResponse response = UserMeResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .profileImage(user.getProfileImage() != null ? user.getProfileImage() : "")
                .shortBio(user.getShortBio() != null ? user.getShortBio() : "")
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileResponse> getUserProfile(@PathVariable String userId) {
        try {
            Long id = Long.parseLong(userId);
            return ResponseEntity.ok(userService.getUserProfile(id));
        } catch (NumberFormatException e) {
            throw new BadRequestException("유효하지 않은 사용자 ID입니다.", "userId");
        }
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserUpdateResponse> updateProfile(
            @PathVariable Long userId,
            @RequestBody @Valid UserUpdateRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        if (userDetails == null) {
            throw new UnauthorizedException("로그인이 필요합니다.", "Authorization");

        }

        String currentEmail = userDetails.getUsername();
        User updatedUser = userService.updateUserProfile(userId, currentEmail, request);
        return ResponseEntity.ok(new UserUpdateResponse(updatedUser));
    }

    @GetMapping("/{userId}/follow")
    public ResponseEntity<FollowListResponse> getFollowList(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit
    ) {
        FollowListResponse response = userService.getFollowingUsers(userId, page, limit);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}/follower")
    public ResponseEntity<FollowListResponse> getFollowerList(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit
    ) {
        FollowListResponse response = userService.getFollowers(userId, page, limit);
        return ResponseEntity.ok(response);
    }

    @GetMapping("{userId}/posts")
    public ResponseEntity<PostListResponse> getUserPublicPosts(
            @PathVariable Long userId,
            @RequestParam(required = false, defaultValue = "") String query,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int limit) {

        if (page < 1) {
            throw new BadRequestException("page는 1 이상의 숫자여야 합니다.", "page");
        }

        if (limit < 1 || limit > 100) {
            throw new BadRequestException("limit은 1에서 100 사이의 숫자여야 합니다.", "limit");
        }

        PostListResponse response = postService.getPublicPostsByUser(userId, query, page, limit);
        return ResponseEntity.ok(response);
    }

}
