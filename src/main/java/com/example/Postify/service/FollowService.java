package com.example.Postify.service;

import com.example.Postify.domain.Follow;
import com.example.Postify.domain.User;
import com.example.Postify.dto.FollowResponse;
import com.example.Postify.exception.BadRequestException;
import com.example.Postify.exception.ConflictException;
import com.example.Postify.exception.UserNotFoundException;
import com.example.Postify.repository.FollowRepository;
import com.example.Postify.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;

    @Transactional
    public FollowResponse followUser(String requesterEmail, Long targetUserId) {

        User follower = userRepository.findByEmail(requesterEmail)
                .orElseThrow(() -> new UserNotFoundException("인증된 사용자를 찾을 수 없습니다."));

        User following = userRepository.findById(targetUserId)
                .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자입니다.", targetUserId));

        if (follower.getId().equals(following.getId())) {
            throw new BadRequestException("자기 자신을 팔로우할 수 없습니다.", "userId");
        }


        if (followRepository.existsByFollowerAndFollowing(follower, following)) {
            throw new ConflictException("이미 팔로우 중인 사용자입니다.");
        }

        Follow follow = Follow.builder()
                .follower(follower)
                .following(following)
                .build();

        followRepository.save(follow);

        return new FollowResponse(true, "팔로우가 완료되었습니다.");
    }

    @Transactional
    public FollowResponse unfollowUser(String requesterEmail, Long targetUserId) {
        User follower = userRepository.findByEmail(requesterEmail)
                .orElseThrow(() -> new UserNotFoundException("인증된 사용자를 찾을 수 없습니다."));

        User following = userRepository.findById(targetUserId)
                .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자입니다.", targetUserId));

        if (follower.getId().equals(following.getId())) {
            throw new BadRequestException("자기 자신을 언팔로우할 수 없습니다.", "userId");
        }

        Follow follow = followRepository.findByFollowerAndFollowing(follower, following)
                .orElseThrow(() -> new ConflictException("이미 언팔로우된 사용자입니다."));

        followRepository.delete(follow);

        return new FollowResponse(true, "언팔로우가 완료되었습니다.");
    }
}
