package com.example.Postify.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PostPageResponse {
    private List<PostResponseDTO> posts;
    private long totalItems;
    private int totalPages;
}
