package com.example.Postify.controller;

import com.example.Postify.domain.Post;
import com.example.Postify.domain.User;
import com.example.Postify.dto.PostPageResponse;
import com.example.Postify.dto.PostResponseDTO;
import com.example.Postify.dto.PostCreateRequest;
import com.example.Postify.dto.PostCreateResponse;
import com.example.Postify.dto.PostViewResponse;
import com.example.Postify.dto.PostUpdateRequest;
import com.example.Postify.dto.PostUpdateResponse;
import com.example.Postify.exception.NotFoundException;
import com.example.Postify.exception.ErrorResponse;
import com.example.Postify.repository.UserRepository;
import com.example.Postify.security.CustomUserDetails;
import com.example.Postify.service.PostService;
import lombok.RequiredArgsConstructor;
import com.example.Postify.exception.BadRequestException;
import com.example.Postify.exception.InternalServerException;
import com.example.Postify.exception.PostNotFoundException;
import com.example.Postify.dto.PostListResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpServerErrorException;

import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;


@Slf4j
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




    @PostMapping
    public ResponseEntity<?> createPost(@RequestBody PostCreateRequest request,
                                        @AuthenticationPrincipal CustomUserDetails userDetails) {

        log.info("✅ createPost 진입");
        if (userDetails == null) {
            log.error("❌ userDetails가 null입니다.");
            throw new AccessDeniedException("인증되지 않은 사용자입니다.");
        }

        log.info("✅ userDetails 객체: {}", userDetails);
        log.info("✅ userDetails.getUser(): {}", userDetails.getUser());

        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new BadRequestException("입력값이 올바르지 않습니다.", "title");
        }

        try {
            Long postId = postService.createPost(request, userDetails.getUser());
            return ResponseEntity.status(201).body(new PostCreateResponse(postId.toString()));
        } catch (Exception e) {
            log.error("❌ 게시글 생성 중 예외 발생", e); // ✅ 예외 발생 로그 찍히게!
            throw new InternalServerException("서버에 문제가 발생했습니다.", "post");
        }
    }

    @GetMapping("/{postId}")
    public ResponseEntity<?> getPost(@PathVariable Long postId) {
        try {
            PostViewResponse response = postService.getPostDetail(postId);
            return ResponseEntity.ok(response);
        } catch (PostNotFoundException  e) {
            throw e;
        } catch (Exception e) {
            throw new InternalServerException("서버에 문제가 발생했습니다.", "postId");
        }
    }
    @PutMapping("/{postId}")
    public ResponseEntity<?> updatePost(@PathVariable Long postId,
                                        @RequestBody PostUpdateRequest request,
                                        @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            postService.updatePost(postId, request, userDetails.getUser());
            return ResponseEntity.ok(new PostUpdateResponse(postId.toString()));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(403).body(
                    new ErrorResponse("AuthorizationError", e.getMessage(), "postId")
            );
        } catch (PostNotFoundException e) {
            throw e;
        } catch (Exception e) {

            return ResponseEntity.status(500).body(
                    new InternalServerException( "서버에 문제가 발생했습니다.")
            );
        }

    }
    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable Long postId,
                                        @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            postService.deletePost(postId, userDetails.getUser());
            return ResponseEntity.ok().body(Map.of("message", "success"));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(403).body(
                    new ErrorResponse("AuthorizationError", e.getMessage(), "postId")
            );
        } catch (PostNotFoundException e) {
            throw e;
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    new InternalServerException( "서버에 문제가 발생했습니다. 잠시 후 다시 시도해주세요.")
            );
        }
    }

}
