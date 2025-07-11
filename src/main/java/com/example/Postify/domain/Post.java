package com.example.Postify.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "posts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 작성자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(length = 300)
    private String preview;  // content 앞부분 자동 저장

    @Column(nullable = false, unique = true)
    private String slug;

    private String thumbnail;

    @Column(nullable = false)
    private boolean isPublished;

    @Column(nullable = false)
    private boolean isTemporary;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private int views = 0;

    @Column(nullable = false)
    private int likes = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "series_id")
    private Series series;

    @Builder
    public Post(User user, String title, String content, String slug,
                String thumbnail, boolean isPublished, boolean isTemporary,
                Series series) {
        this.user = user;
        this.title = title;
        this.content = content;
        this.preview = generatePreview(content);
        this.slug = slug;
        this.thumbnail = thumbnail;
        this.isPublished = isPublished;
        this.isTemporary = isTemporary;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.series = series;
        this.views = 0;
        this.likes = 0;
    }

    private String generatePreview(String content) {
        if (content == null) return "";
        return content.length() <= 300 ? content : content.substring(0, 300);
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}