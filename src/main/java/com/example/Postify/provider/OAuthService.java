package com.example.Postify.provider;

public interface OAuthService {
    OAuthProviderType getProviderType();
    OAuthUserInfo getUserInfo(String accessToken);
}
