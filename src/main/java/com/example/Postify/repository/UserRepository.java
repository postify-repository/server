package com.example.Postify.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.Postify.entity.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}