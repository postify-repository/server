package com.example.Postify.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Pagination {
    private int page;
    private int limit;
    private int totalPages;
    private long totalItems;
    private boolean hasNext;
}
