package com.example.Postify.service;

import com.example.Postify.domain.Post;
import com.example.Postify.domain.Series;
import com.example.Postify.domain.User;
import com.example.Postify.dto.Pagination;
import com.example.Postify.dto.PostListResponse;
import com.example.Postify.dto.PostSummaryDto;
import com.example.Postify.dto.PostCreateRequest;
import com.example.Postify.dto.PostViewResponse;
import com.example.Postify.dto.PostUpdateRequest;
import com.example.Postify.exception.UserNotFoundException;
import com.example.Postify.exception.PostNotFoundException;
import com.example.Postify.exception.NotFoundException;
import com.example.Postify.repository.CommentRepository;
import com.example.Postify.repository.FollowRepository;
import com.example.Postify.repository.PostRepository;
import com.example.Postify.repository.UserRepository;
import com.example.Postify.repository.SeriesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;



@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final CommentRepository commentRepository;
    private final SeriesRepository seriesRepository;

    // 최신 글 조회
    public Page<Post> getLatestPosts(int page, int limit) {
        return postRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(page, limit));
    }

    // 트렌딩 글 조회
    public Page<Post> getTrendingPosts(String period, int page, int limit) {
        LocalDateTime startDate = switch (period) {
            case "day" -> LocalDateTime.now().minusDays(1);
            case "month" -> LocalDateTime.now().minusMonths(1);
            case "year" -> LocalDateTime.now().minusYears(1);
            default -> LocalDateTime.now().minusWeeks(1);
        };
        return postRepository.findTrendingPosts(startDate, PageRequest.of(page, limit));
    }

    // 피드 글 조회 (팔로우한 사용자들 글)
    public Page<Post> getFeedPosts(Long currentUserId, int page, int limit) {
        List<Long> followedUserIds = followRepository.findFollowingUserIds(currentUserId);

        if (followedUserIds.isEmpty()) {
            return Page.empty();
        }

        Pageable pageable = PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        return postRepository.findFeedPosts(followedUserIds, pageable);
    }

    // 내 글 목록 조회
    public Page<Post> getMyPosts(Long userId, String query, int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        if (query == null || query.isBlank()) {
            return postRepository.findByUserId(userId, pageable);
        } else {
            return postRepository.findByUserIdAndTitleContainingIgnoreCase(userId, query, pageable);
        }
    }

    // 특정 사용자의 공개 글 목록 조회
    public PostListResponse getPublicPostsByUser(Long userId, String query, int page, int limit) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자입니다."));

        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Post> postsPage = postRepository.findPublicPostsByUser(user, query, pageable);

        List<PostSummaryDto> postDtos = postsPage.getContent().stream()
                .map(post -> {
                    int commentCount = commentRepository.countByPost(post);
                    return PostSummaryDto.from(post, commentCount);
                })
                .toList();

        Pagination pagination = new Pagination(
                postsPage.getNumber() + 1,
                postsPage.getSize(),
                postsPage.getTotalPages(),
                postsPage.getTotalElements(),
                postsPage.hasNext()
        );



        return new PostListResponse(postDtos, pagination);
    }


    @Transactional
    public Long createPost(PostCreateRequest request, User user) {


        Series series = null;
        if (request.getSeriesId() != null) {
            series = seriesRepository.findById(Long.valueOf(request.getSeriesId()))
                    .orElseThrow(() -> new NotFoundException("존재하지 않는 시리즈입니다."));
        }

        String slug = generateSlug(request.getTitle());

        Post post = Post.builder()
                .user(user)
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
                .orElseThrow(() -> new PostNotFoundException(postId));

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
        String baseSlug = title.toLowerCase().replaceAll("\\s+", "-");
        String slug = baseSlug;
        int count = 0;

        while (postRepository.existsBySlug(slug)) {
            count++;
            slug = baseSlug + "-" + count;
        }

        return slug;
    }

    @Transactional
    public void updatePost(Long postId, PostUpdateRequest request, User user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));

        if (!post.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("작성자만 수정할 수 있습니다.");
        }

        Series series = null;
        if (request.getSeriesId() != null) {
            series = seriesRepository.findById(Long.parseLong(request.getSeriesId()))
                    .orElseThrow(() -> new NotFoundException("해당 시리즈가 존재하지 않습니다."));
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
                .orElseThrow(() -> new PostNotFoundException(postId));

        if (!post.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("작성자만 삭제할 수 있습니다.");
        }

        postRepository.delete(post);
    }

}
