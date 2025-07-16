package com.example.Postify.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.Postify.domain.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByNickname(String nickname);
}