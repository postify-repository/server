package com.example.Postify.config;

import com.example.Postify.domain.Post;
import com.example.Postify.domain.User;
import com.example.Postify.repository.PostRepository;
import com.example.Postify.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class DummyDataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Override
    public void run(String... args) {
        // 이미 게시글이 있으면 더미 데이터 삽입 안 함
        if (postRepository.count() > 0) return;

        // 유저 생성 or 조회
        User user = userRepository.findByEmail("dummy@test.com")
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .email("dummy@test.com")
                            .passwordHash("$2a$10$UJ1KnixpJUzOjOGxYkvdT.oJ5EyLMDbKfGM9uYgK9nsAyXf5CxlO6") // "1234" 암호화
                            .nickname("더미유저")
                            .username("dummyuser")
                            .build();
                    return userRepository.save(newUser);
                });

        // 게시글 10개 더미 생성
        IntStream.rangeClosed(1, 10).forEach(i -> {
            Post post = Post.builder()
                    .user(user)
                    .title("테스트 제목 " + i)
                    .content("테스트 내용 " + i + "입니다. 이 게시글은 더미 테스트용입니다.")
                    .slug("dummy-post-" + i)
                    .thumbnail(null)
                    .isPublished(true)
                    .isTemporary(false)
                    .series(null)
                    .build();
            postRepository.save(post);
        });

        System.out.println("✅ 테스트용 더미 게시글 10개 삽입 완료!");
    }
}
