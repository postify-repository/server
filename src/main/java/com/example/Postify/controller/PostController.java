package com.example.Postify.controller;

import java.util.Map;
import com.example.Postify.dto.*;
import com.example.Postify.security.UserDetailsImpl;
import com.example.Postify.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<?> createPost(@RequestBody PostCreateRequest request,
                                        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(
                        new ErrorResponse("ValidationError", "입력값이 올바르지 않습니다.", "title")
                );
            }

            Long postId = postService.createPost(request, userDetails.getUser());
            return ResponseEntity.status(201).body(new PostCreateResponse(postId.toString()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    new ErrorResponse("InternalServerError", "서버에 문제가 발생했습니다.")
            );
        }
    }

    @GetMapping("/{postId}")
    public ResponseEntity<?> getPost(@PathVariable Long postId) {
        try {
            PostViewResponse response = postService.getPostDetail(postId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(
                    new ErrorResponse("NotFoundError", "게시물을 찾을 수 없습니다.", "postId")
            );
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    new ErrorResponse("InternalServerError", "서버에 문제가 발생했습니다.")
            );
        }
    }
    @PutMapping("/{postId}")
    public ResponseEntity<?> updatePost(@PathVariable Long postId,
                                        @RequestBody PostUpdateRequest request,
                                        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            postService.updatePost(postId, request, userDetails.getUser());
            return ResponseEntity.ok(new PostUpdateResponse(postId.toString()));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(403).body(
                    new ErrorResponse("AuthorizationError", e.getMessage(), "postId")
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(
                    new ErrorResponse("NotFoundError", e.getMessage(), "postId")
            );
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    new ErrorResponse("InternalServerError", "서버에 문제가 발생했습니다.")
            );
        }

    }
    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable Long postId,
                                        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            postService.deletePost(postId, userDetails.getUser());
            return ResponseEntity.ok().body(Map.of("message", "success"));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(403).body(
                    new ErrorResponse("AuthorizationError", e.getMessage(), "postId")
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(
                    new ErrorResponse("NotFoundError", e.getMessage(), "postId")
            );
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    new ErrorResponse("InternalServerError", "서버에 문제가 발생했습니다. 잠시 후 다시 시도해주세요.")
            );
        }
    }
}