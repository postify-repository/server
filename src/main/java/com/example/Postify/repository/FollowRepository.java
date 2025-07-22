package com.example.Postify.repository;

import com.example.Postify.domain.Follow;
import com.example.Postify.domain.Post;
import com.example.Postify.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {

    @Query("SELECT f.following.id FROM Follow f WHERE f.follower.id = :userId")
    List<Long> findFollowingUserIds(@Param("userId") Long userId);

    @Query("SELECT p FROM Post p WHERE p.user.id IN :userIds AND p.isPublished = true AND p.isTemporary = false ORDER BY p.createdAt DESC")
    Page<Post> findFeedPosts(@Param("userIds") List<Long> userIds, Pageable pageable);

    @Query("SELECT f.following FROM Follow f WHERE f.follower.id = :userId")
    Page<User> findFollowingUsers(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT f.follower FROM Follow f WHERE f.following.id = :userId")
    Page<User> findFollowersByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT COUNT(f) > 0 FROM Follow f WHERE f.follower = :follower AND f.following = :following")
    boolean existsByFollowerAndFollowing(@Param("follower") User follower, @Param("following") User following);

    Optional<Follow> findByFollowerAndFollowing(User follower, User following);


}

