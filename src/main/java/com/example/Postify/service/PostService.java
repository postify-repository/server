package com.example.Postify.service;

import com.example.Postify.domain.Post;
import com.example.Postify.domain.Series;
import com.example.Postify.domain.User;
import com.example.Postify.dto.PostCreateRequest;
import com.example.Postify.dto.PostViewResponse;
import com.example.Postify.dto.PostUpdateRequest;
import com.example.Postify.repository.PostRepository;
import com.example.Postify.repository.SeriesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final SeriesRepository seriesRepository;

    @Transactional
    public Long createPost(PostCreateRequest request, User user) {
        Series series = null;
        if (request.getSeriesId() != null) {
            series = seriesRepository.findById(Long.valueOf(request.getSeriesId()))
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 시리즈입니다."));
        }

        String slug = generateSlug(request.getTitle());

        Post post = Post.builder()
                .user(user)  // 로그인한 유저 객체
                .title(request.getTitle())
                .content(request.getContent())
                .slug(slug)
                .thumbnail(request.getThumbnail())
                .isPublished(request.isPublic())
                .isTemporary(request.isTemporary())
                .series(series)
                .tags(request.getTags())
                .build();

        postRepository.save(post);
        return post.getId();
    }

    @Transactional(readOnly = true)
    public PostViewResponse getPostDetail(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시물을 찾을 수 없습니다."));

        return PostViewResponse.builder()
                .title(post.getTitle())
                .tags(post.getTags())
                .toc(Collections.emptyList())
                .comments(Collections.emptyList())
                .like(post.getLikes())
                .socialLinks(Collections.emptyList())
                .prevPost(null)
                .nextPost(null)
                .build();
    }



    private String generateSlug(String title) {
        return title.toLowerCase().replaceAll("\\s+", "-") + "-" + System.currentTimeMillis();
    }

    @Transactional
    public void updatePost(Long postId, PostUpdateRequest request, User user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        if (!post.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("작성자만 수정할 수 있습니다.");
        }

        Series series = null;
        if (request.getSeriesId() != null) {
            series = seriesRepository.findById(Long.parseLong(request.getSeriesId()))
                    .orElseThrow(() -> new IllegalArgumentException("해당 시리즈가 존재하지 않습니다."));
        }

        post.update(
                request.getTitle(),
                request.getContent(),
                request.getTags(),
                request.getThumbnail(),
                series,
                request.isPublic(),
                request.isTemporary()
        );
    }

    @Transactional
    public void deletePost(Long postId, User user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        if (!post.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("작성자만 삭제할 수 있습니다.");
        }

        postRepository.delete(post);
    }

}