package com.example.Postify.provider;

public enum OAuthProviderType {
    GITHUB;

    public static OAuthProviderType from(String value) {
        try {
            return OAuthProviderType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("지원하지 않는 소셜 로그인 제공자입니다: " + value);
        }
    }
}
