package com.example.Postify.util;

import com.example.Postify.domain.Follow;
import com.example.Postify.domain.User;
import com.example.Postify.repository.FollowRepository;
import com.example.Postify.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FollowTestDataInitializer {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    public void createTestFollowRelation() {
        User follower = userRepository.findById(5L)
                .orElseThrow(() -> new IllegalArgumentException("follower not found"));

        User following = userRepository.findById(2L)
                .orElseThrow(() -> new IllegalArgumentException("following not found"));

        Follow follow = Follow.builder()
                .follower(follower)
                .following(following)
                .build();

        followRepository.save(follow);
    }
}
