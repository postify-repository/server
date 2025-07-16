package com.example.Postify.repository;

import com.example.Postify.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    Optional<Post> findBySlug(String slug);  // slug로 게시글 검색할 때 사용
    boolean existsBySlug(String slug);       // slug 중복 검사
}