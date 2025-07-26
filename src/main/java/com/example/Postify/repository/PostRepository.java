package com.example.Postify.repository;

import com.example.Postify.domain.Post;
import com.example.Postify.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT p FROM Post p WHERE p.isPublished = true AND p.isTemporary = false ORDER BY p.createdAt DESC")
    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.isPublished = true AND p.isTemporary = false AND p.createdAt >= :startDate ORDER BY p.likes DESC")
    Page<Post> findTrendingPosts(@Param("startDate") LocalDateTime startDate, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.user.id IN :followedUserIds AND p.isPublished = true AND p.isTemporary = false ORDER BY p.createdAt DESC")
    Page<Post> findFeedPosts(@Param("followedUserIds") List<Long> followedUserIds, Pageable pageable);

    Page<Post> findByUserId(Long userId, Pageable pageable);

    Page<Post> findByUserIdAndTitleContainingIgnoreCase(Long userId, String query, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.user = :user AND p.isPublished = true AND " +
            "(LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(p.content) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Post> findPublicPostsByUser(@Param("user") User user,
                                     @Param("query") String query,
                                     Pageable pageable);

    // slug 중복 확인용
    boolean existsBySlug(String slug);
}
