package com.example.Postify.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class PostViewResponse {
    private String title;
    private List<String> tags;
    private List<String> toc;
    private List<CommentResponse> comments;
    private int like;
    private List<String> socialLinks;
    private String prevPost;
    private String nextPost;

    @Data
    @AllArgsConstructor
    public static class CommentResponse {
        private String author;
        private String content;
    }
}