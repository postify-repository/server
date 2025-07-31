package com.example.Postify.jwt;

import com.example.Postify.config.JwtProperties;
import com.example.Postify.exception.InvalidTokenException;
import com.example.Postify.exception.TokenExpiredException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@RequiredArgsConstructor
@Component
public class JwtUtil {

    private final JwtProperties jwtProperties;


    @Value("${jwt.secret}")
    private String secret;

    private Key key;

    private final long accessTokenValidity = 1000 * 60 * 60 * 1; // 1시간
    private final long refreshTokenValidity = 1000 * 60 * 60 * 24 * 14; // 2주

    @PostConstruct
    public void init() {
        String secret = jwtProperties.getSecret();
        if (secret == null) {
            System.out.println("❌ jwt.secret is null! Check your config and @EnableConfigurationProperties.");
        } else {
            System.out.println("✅ jwt.secret loaded: " + secret);
            this.key = Keys.hmacShaKeyFor(secret.getBytes());
        }
    }



    // Access Token 생성
    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenValidity))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // Refresh Token 생성
    public String generateRefreshToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenValidity))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 토큰에서 이메일(subject) 추출
    public String extractEmail(String token) {
        return getClaims(token).getSubject();
    }

    // 유효성 검사
    public boolean isTokenValid(String token) {
        try {
            getClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // 토큰 만료 여부 검사
    public boolean isTokenExpired(String token) {
        try {
            return getClaims(token).getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    public String refreshAccessToken(String refreshToken) {
        try {
            // 1. refresh token 유효성 검증
            Claims claims = getClaims(refreshToken);; // 유효하면 claims 반환, 아니면 예외 발생

            // 2. 만료 여부 확인
            if (isTokenExpired(refreshToken)) {
                throw new TokenExpiredException("refresh token이 만료되었습니다.", "refreshToken");
            }

            // 3. 새로운 access token 발급
            String email = claims.getSubject();
            return generateToken(email); // 새로운 access token 반환

        } catch (ExpiredJwtException e) {
            throw new TokenExpiredException("refresh token이 만료되었습니다.", "refreshToken");
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidTokenException("유효하지 않은 refresh token입니다.", "refreshToken");
        }
    }


    // Claims 파싱
    private Claims getClaims(String token) {
        try {
            System.out.println(">>>> Verifying token: " + token);
            return Jwts.parserBuilder()
                    .setSigningKey(this.key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            System.out.println(">>>> JWT verification failed: " + e.getMessage());
            throw e;
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }







}
