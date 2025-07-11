package com.example.Postify.repository;

import com.example.Postify.domain.Follow;
import com.example.Postify.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    @Query("SELECT f.following.id FROM Follow f WHERE f.follower.id = :userId")
    List<Long> findFollowingUserIds(@Param("userId") Long userId);

    @Query("SELECT p FROM Post p WHERE p.user.id IN :userIds AND p.isPublished = true AND p.isTemporary = false ORDER BY p.createdAt DESC")
    Page<Post> findFeedPosts(@Param("userIds") List<Long> userIds, Pageable pageable);
}
