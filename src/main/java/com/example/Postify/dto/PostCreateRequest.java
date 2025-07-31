package com.example.Postify.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class PostCreateRequest {
    private String title;
    private String content;
    private List<String> tags;
    private String thumbnail;
    private Long seriesId; // 문자열로 넘어오지만 Long으로 처리
    private boolean isPublic;
    private boolean isTemporary;
}