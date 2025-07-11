package com.example.Postify.service;

import com.example.Postify.domain.Post;
import com.example.Postify.repository.FollowRepository;
import com.example.Postify.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final FollowRepository followRepository;

    public Page<Post> getLatestPosts(int page, int limit) {
        return postRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(page, limit));
    }

    public Page<Post> getTrendingPosts(String period, int page, int limit) {
        LocalDateTime startDate = switch (period) {
            case "day" -> LocalDateTime.now().minusDays(1);
            case "month" -> LocalDateTime.now().minusMonths(1);
            case "year" -> LocalDateTime.now().minusYears(1);
            default -> LocalDateTime.now().minusWeeks(1);
        };
        return postRepository.findTrendingPosts(startDate, PageRequest.of(page, limit));
    }

    public Page<Post> getFeedPosts(Long currentUserId, int page, int limit) {
        List<Long> followedUserIds = followRepository.findFollowingUserIds(currentUserId);

        if (followedUserIds.isEmpty()) {
            return Page.empty();
        }

        Pageable pageable = PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        return postRepository.findFeedPosts(followedUserIds, pageable);
    }
}
