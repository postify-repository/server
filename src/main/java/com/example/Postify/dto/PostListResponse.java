package com.example.Postify.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PostListResponse {
    private List<PostSummaryDto> posts;
    private Pagination pagination;
}
