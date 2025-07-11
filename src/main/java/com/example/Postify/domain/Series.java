package com.example.Postify.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "series")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Series {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String seriesName;

    @Column(nullable = false, unique = true)
    private String seriesUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "series", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt ASC")
    private List<Post> posts;

    @Builder
    public Series(String seriesName, String seriesUrl, User user) {
        this.seriesName = seriesName;
        this.seriesUrl = seriesUrl;
        this.user = user;
    }
}