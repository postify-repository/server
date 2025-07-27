package com.example.Postify.controller;

import com.example.Postify.domain.User;
import com.example.Postify.dto.*;
import com.example.Postify.exception.*;
import com.example.Postify.dto.EmailCheckRequest;
import com.example.Postify.jwt.JwtUtil;
import com.example.Postify.security.CustomUserDetails;
import com.example.Postify.service.AuthService;
import com.example.Postify.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthService authService;
    private final JwtUtil jwtUtil;

    @PostMapping("/signup")
    public ResponseEntity<MessageResponse> signup(@RequestBody UserSignupRequest request) {
        userService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new MessageResponse("회원가입이 완료되었습니다."));
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponse> login(@RequestBody UserLoginRequest request,
                                                   HttpServletResponse response) {
        try {
            if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
                throw new MissingFieldException("email은 필수 입력 항목입니다.", "email");
            }
            if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                throw new MissingFieldException("password는 필수 입력 항목입니다.", "password");
            }

            User user = userService.findByEmail(request.getEmail());

            if (!authService.passwordMatches(request.getPassword(), user.getPasswordHash())) {
                throw new UnauthorizedException("이메일 또는 비밀번호가 올바르지 않습니다.");
            }

            String accessToken = jwtUtil.generateToken(user.getId().toString()); // or user.getEmail()
            String refreshToken = jwtUtil.generateRefreshToken(user.getId().toString()); // or user.getEmail()

            // ✅ RefreshToken을 HttpOnly 쿠키로 내려줌
            ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(7 * 24 * 60 * 60) // 7일
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

            UserLoginResponse loginResponse = new UserLoginResponse(
                    accessToken,
                    user.getUsername(),
                    user.getEmail(),
                    user.getNickname(),
                    user.getId().toString(), // Long → String
                    user.getShortBio()
            );


            return ResponseEntity.ok(loginResponse);

        } catch (MissingFieldException | ValidationException e) {
            throw e; // GlobalExceptionHandler에서 처리
        } catch (UserNotFoundException | UnauthorizedException e) {
            throw new UnauthorizedException("이메일 또는 비밀번호가 올바르지 않습니다.");
        } catch (Exception e) {
            throw new InternalServerException("서버에 문제가 발생했습니다. 잠시 후 다시 시도해주세요.");
        }
    }





    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                  HttpServletResponse response) {
        authService.logout(userDetails.getUser().getEmail());

        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);

        response.addCookie(cookie);

        return ResponseEntity.ok(new MessageResponse("로그아웃 되었습니다."));
    }


    // 회원 탈퇴
    @DeleteMapping("/account")
    public ResponseEntity<MessageResponse> deleteAccount(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                         @Valid @RequestBody DeleteUserRequest request) {
        userService.deleteUser(userDetails.getUser(), request.getPassword());
        return ResponseEntity.ok(new MessageResponse("계정이 삭제되었습니다."));
    }


    @PostMapping("/refresh-token")
    public ResponseEntity<TokenRefreshResponse> refreshToken(
            @CookieValue(name = "refreshToken", required = false) String refreshToken
    ) {
        if (refreshToken == null) {
            throw new InvalidTokenException("유효하지 않은 refresh token입니다.", "refreshToken");
        }

        String newAccessToken = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(new TokenRefreshResponse(newAccessToken));
    }

    @PostMapping("/email-check")
    public ResponseEntity<EmailCheckResponse> checkEmail(
            @Valid @RequestBody EmailCheckRequest request
    ) {
        boolean isAvailable = !userService.existsByEmail(request.getEmail());

        if (!isAvailable) {
            throw new ConflictException("이미 사용 중인 이메일입니다.", "email");
        }

        return ResponseEntity.ok(new EmailCheckResponse(true, "사용 가능한 이메일입니다."));
    }




}