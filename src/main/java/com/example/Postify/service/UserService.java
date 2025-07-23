package com.example.Postify.service;

import com.example.Postify.domain.User;
import com.example.Postify.dto.*;
import com.example.Postify.exception.*;
import com.example.Postify.provider.OAuthUserInfo;
import com.example.Postify.repository.FollowRepository;
import com.example.Postify.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.Postify.provider.OAuthProviderType;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public void signup(UserSignupRequest request) {
        // 이메일 중복 확인
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException("이미 등록된 이메일입니다.");
        }

        // 닉네임 중복 확인
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new DuplicateNicknameException("이미 사용 중인 닉네임입니다.", "nickname");
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // User 엔티티 생성
        User user = User.builder()
                .username(request.getUsername())
                .nickname(request.getNickname())
                .displayName(request.getDisplayName())
                .email(request.getEmail())
                .passwordHash(encodedPassword)
                .shortBio(request.getShortBio())
                .profileImage(request.getProfileImage())
                .socialLinks(request.getSocialLinks())
                .build();

        // 저장
        userRepository.save(user);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("해당 사용자를 찾을 수 없습니다: " + email));
    }


    // 회원 탈퇴
    public void deleteUser(User user, String rawPassword) {
        if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            throw new BadRequestException("비밀번호가 올바르지 않습니다.", "password");
        }
        userRepository.delete(user);
    }
    public User findByProviderOrThrow(OAuthProviderType provider, String providerId) {
        return userRepository.findByProviderAndProviderId(provider.name(), providerId)
                .orElseThrow(() -> new RuntimeException("소셜 인증에 실패했습니다."));
    }

    public User createSocialUser(OAuthUserInfo userInfo, SocialRegisterRequest request) {
        User user = User.builder()
                .email(userInfo.getEmail())
                .provider(userInfo.getProvider().name())
                .providerId(userInfo.getProviderId())
                .username(request.getUsername())
                .nickname(request.getNickname())
                .passwordHash("SOCIAL_LOGIN") // 임시 패스워드
                .displayName(request.getUsername())
                .shortBio(request.getBio())
                .build();


        return userRepository.save(user);
    }

    public UserProfileResponse getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자입니다.", userId));
        return new UserProfileResponse(user);
    }

    @Transactional
    public User updateUserProfile(Long userId, String currentEmail, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자입니다.", userId));

        if (!user.getEmail().equals(currentEmail)) {
            throw new ForbiddenException("다른 사용자의 프로필을 수정할 수 없습니다.");
        }

        if (!user.getNickname().equals(request.getNickname())
                && userRepository.existsByNickname(request.getNickname())) {
            throw new DuplicateNicknameException("이미 사용 중인 닉네임입니다.", "nickname");
        }

        user.updateProfile(request.getNickname(), request.getBio(), request.getProfileImage());
        return user;
    }

    public FollowListResponse getFollowingUsers(Long userId, int page, int limit) {
        if (page < 1) {
            throw new BadRequestException("page는 1 이상의 숫자여야 합니다.", "page");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자입니다.", userId));

        Pageable pageable = PageRequest.of(page - 1, limit);
        Page<User> followings = followRepository.findFollowingUsers(userId, pageable);

        List<FollowUserResponse> userList = followings.getContent().stream()
                .map(u -> new FollowUserResponse(
                        u.getId(),
                        u.getNickname(),
                        u.getProfileImage(),
                        u.getShortBio()
                ))
                .toList();

        return new FollowListResponse(
                followings.getTotalElements(),
                userList,
                new FollowListResponse.Pagination(
                        page,
                        limit,
                        followings.getTotalPages(),
                        followings.getTotalElements(),
                        followings.hasNext()
                )
        );
    }

    @Transactional(readOnly = true)
    public FollowListResponse getFollowers(Long userId, int page, int limit) {
        if (page < 1 || limit < 1 || limit > 100) {
            throw new BadRequestException("Limit은 1에서 100 사이의 숫자여야 합니다.", "limit");
        }


        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자입니다.", userId));

        Pageable pageable = PageRequest.of(page - 1, limit);
        Page<User> followers = followRepository.findFollowersByUserId(userId, pageable);

        List<FollowUserResponse> userList = followers.getContent().stream()
                .map(u -> new FollowUserResponse(
                        u.getId(),
                        u.getNickname(),
                        u.getProfileImage(),
                        u.getShortBio()
                ))
                .toList();

        return new FollowListResponse(
                followers.getTotalElements(),
                userList,
                new FollowListResponse.Pagination(
                        page,
                        limit,
                        followers.getTotalPages(),
                        followers.getTotalElements(),
                        followers.hasNext()
                )
        );


    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }









}

