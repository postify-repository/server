package com.example.Postify.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "posts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(length = 300)
    private String preview;

    @Column(nullable = false, unique = true)
    private String slug;

    @ElementCollection
    @CollectionTable(name = "post_tags", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "tag")
    private List<String> tags = new ArrayList<>();

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
                Series series, List<String> tags) {
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
        this.tags = tags != null ? tags : new ArrayList<>();
    }

    private String generatePreview(String content) {
        if (content == null) return "";
        return content.length() <= 300 ? content : content.substring(0, 300);
    }

    public void update(String title, String content, List<String> tags, String thumbnail,
                       Series series, boolean isPublished, boolean isTemporary) {
        this.title = title;
        this.content = content;
        this.tags = tags;
        this.preview = generatePreview(content);
        this.thumbnail = thumbnail;
        this.series = series;
        this.isPublished = isPublished;
        this.isTemporary = isTemporary;
        this.updatedAt = LocalDateTime.now();
    }
}