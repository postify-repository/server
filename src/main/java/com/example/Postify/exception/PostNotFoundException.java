package com.example.Postify.exception;

public class PostNotFoundException extends RuntimeException {
    private final String field = "postId";

    public PostNotFoundException(Long postId) {
        super("게시글이 존재하지 않습니다.");
    }

    public String getField() {
        return field;
    }
}