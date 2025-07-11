package com.example.Postify.dto;

import com.example.Postify.domain.Post;
import lombok.Getter;

@Getter
public class PostResponseDTO {

    private Long id;
    private String title;
    private String preview;
    private String thumbnail;
    private boolean isPublished;
    private boolean isTemporary;
    private int views;
    private int likes;
    private String slug;
    private AuthorDTO author;  // 작성자 정보

    public static PostResponseDTO fromEntity(Post post) {
        PostResponseDTO dto = new PostResponseDTO();
        dto.id = post.getId();
        dto.title = post.getTitle();
        dto.preview = post.getPreview();
        dto.thumbnail = post.getThumbnail();
        dto.isPublished = post.isPublished();
        dto.isTemporary = post.isTemporary();
        dto.views = post.getViews();
        dto.likes = post.getLikes();
        dto.slug = post.getSlug();
        dto.author = new AuthorDTO(post.getUser());
        return dto;
    }

    @Getter
    public static class AuthorDTO {
        private Long id;
        private String email;
        private String username;

        public AuthorDTO(com.example.Postify.domain.User user) {
            this.id = user.getId();
            this.email = user.getEmail();
            this.username = user.getUsername();
        }
    }
}
 // AuthorDTO : 게시글의 작성자 정보를 담기 위해 필요한 서브 DTO
// PostResponseDTO 안에서만 사용되므로, 내부 클래스로 제한해서 설계함