package com.example.Postify.dto;

import com.example.Postify.domain.User;
import lombok.Getter;

import java.time.format.DateTimeFormatter;
import java.util.Map;

@Getter
public class UserUpdateResponse {

    private final String message = "프로필이 성공적으로 수정되었습니다.";

    private final UserInfo user;

    public UserUpdateResponse(User user) {
        this.user = new UserInfo(user);
    }

    @Getter
    public static class UserInfo {
        private Long userId;
        private String username;
        private String nickname;
        private String email;
        private String bio;
        private String profileImage;
        private Map<String, String> socialLinks;
        private String updatedAt;

        public UserInfo(User user) {
            this.userId = user.getId();
            this.username = user.getUsername();
            this.nickname = user.getNickname();
            this.email = user.getEmail();
            this.bio = user.getShortBio();
            this.profileImage = user.getProfileImage();
            this.socialLinks = user.getSocialLinks();
            this.updatedAt = user.getUpdatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }
    }
}
