package com.example.Postify.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class FollowListResponse {
    private long total;
    private List<FollowUserResponse> users;
    private Pagination pagination;

    @Getter
    @AllArgsConstructor
    public static class Pagination {
        private int page;
        private int limit;
        private int totalPages;
        private long totalItems;
        private boolean hasNext;
    }
}
