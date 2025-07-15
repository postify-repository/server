package com.example.Postify.repository;

import com.example.Postify.domain.Post;
import com.example.Postify.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    int countByPost(Post post);
}
