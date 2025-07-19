package com.example.Postify.provider;

import com.example.Postify.domain.User;
import com.example.Postify.dto.SocialLoginRequest;
import com.example.Postify.dto.SocialLoginResponse;
import com.example.Postify.dto.SocialRegisterRequest;
import com.example.Postify.jwt.JwtProvider;
import com.example.Postify.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class OAuthAuthController {

    private final OAuthServiceManager serviceManager;
    private final UserService userService;
    private final JwtProvider jwtProvider;

    @PostMapping("/social")
    public ResponseEntity<?> register(@RequestBody SocialRegisterRequest request,
                                      @RequestHeader("Authorization") String tokenHeader) {
        String accessToken = extractToken(tokenHeader);
        OAuthUserInfo userInfo = serviceManager.fetchUserInfo(request.getProvider(), accessToken);

        if (userService.existsByEmail(userInfo.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "ConflictError", "message", "이미 등록된 사용자입니다.", "field", "email"));
        }

        userService.createSocialUser(userInfo, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "회원가입이 완료되었습니다."));
    }

    @PostMapping("/social-login")
    public ResponseEntity<?> login(@RequestBody SocialLoginRequest request,
                                   @RequestHeader("Authorization") String tokenHeader) {
        String accessToken = extractToken(tokenHeader);
        OAuthUserInfo userInfo = serviceManager.fetchUserInfo(request.getProvider(), accessToken);

        User user = userService.findByProviderOrThrow(userInfo.getProvider(), userInfo.getProviderId());
        String jwt = jwtProvider.generateToken(user.getEmail());

        return ResponseEntity.ok(new SocialLoginResponse(
                jwt, user.getUsername(), user.getEmail(), user.getNickname(),
                user.getId().toString(), user.getShortBio()
        ));
    }

    private String extractToken(String header) {
        return header.replace("Bearer ", "").trim();
    }
}
