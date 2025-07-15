package com.example.Postify.controller;

import com.example.Postify.domain.Post;
import com.example.Postify.domain.User;
import com.example.Postify.dto.PostPageResponse;
import com.example.Postify.dto.PostResponseDTO;
import com.example.Postify.repository.UserRepository;
import com.example.Postify.service.PostService;
import lombok.RequiredArgsConstructor;
import com.example.Postify.exception.BadRequestException;
import com.example.Postify.dto.PostListResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;
    private final UserRepository userRepository;

    @GetMapping("/latest")
    public ResponseEntity<List<PostResponseDTO>> getLatestPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit
    ) {
        Page<Post> posts = postService.getLatestPosts(page, limit);
        List<PostResponseDTO> result = posts.getContent().stream()
                .map(PostResponseDTO::fromEntity)
                .toList();

        return ResponseEntity.ok(result);
    }

    @GetMapping("/trending")
    public ResponseEntity<List<PostResponseDTO>> getTrendingPosts(
            @RequestParam(defaultValue = "week") String period,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit
    ) {
        Page<Post> posts = postService.getTrendingPosts(period, page, limit);
        List<PostResponseDTO> result = posts.getContent().stream()
                .map(PostResponseDTO::fromEntity)
                .toList();

        return ResponseEntity.ok(result);
    }

    @GetMapping("/feed")
    public ResponseEntity<List<PostResponseDTO>> getFeedPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // JWT에서 파싱된 이메일

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Page<Post> posts = postService.getFeedPosts(currentUser.getId(), page, limit);
        List<PostResponseDTO> result = posts.getContent().stream()
                .map(PostResponseDTO::fromEntity)
                .toList();

        return ResponseEntity.ok(result);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMyPosts(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit
    ) {
        Page<Post> posts = postService.getMyPosts(currentUser.getId(), query, page, limit);
        List<PostResponseDTO> result = posts.getContent().stream()
                .map(PostResponseDTO::fromEntity)
                .toList();

        return ResponseEntity.ok(new PostPageResponse(result, posts.getTotalElements(), posts.getTotalPages()));
    }

    @GetMapping("/{userId}")
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
