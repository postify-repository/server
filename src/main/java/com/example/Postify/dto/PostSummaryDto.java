package com.example.Postify.dto;

import com.example.Postify.domain.Post;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class PostSummaryDto {
    private Long id;
    private String title;
    private String content;
    private boolean isPublic;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int views;
    private int likes;
    private int commentsCount;
    private List<String> tags;
    private String seriesId;
    private String thumbnail;

    public static PostSummaryDto from(Post post, int commentCount) {
        return new PostSummaryDto(
                post.getId(),
                post.getTitle(),
                post.getPreview(),
                post.isPublished(),
                post.getCreatedAt(),
                post.getUpdatedAt(),
                post.getViews(),
                post.getLikes(),
                commentCount,
                List.of(),  // 태그 연결 시 수정
                post.getSeries() != null ? post.getSeries().getId().toString() : null,
                post.getThumbnail()
        );
    }

    public PostSummaryDto(Long id, String title, String content, boolean isPublic, LocalDateTime createdAt,
                          LocalDateTime updatedAt, int views, int likes, int commentsCount,
                          List<String> tags, String seriesId, String thumbnail) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.isPublic = isPublic;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.views = views;
        this.likes = likes;
        this.commentsCount = commentsCount;
        this.tags = tags;
        this.seriesId = seriesId;
        this.thumbnail = thumbnail;
    }
}
