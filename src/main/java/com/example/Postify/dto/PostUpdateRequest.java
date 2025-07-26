package com.example.Postify.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class PostUpdateRequest {
    private String title;
    private String content;
    private List<String> tags;
    private String thumbnail;
    private String seriesId;
    private boolean isPublic;
    private boolean isTemporary;
}