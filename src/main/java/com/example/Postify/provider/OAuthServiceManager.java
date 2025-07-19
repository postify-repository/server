package com.example.Postify.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OAuthServiceManager {

    private final List<OAuthService> services;

    public OAuthUserInfo fetchUserInfo(String providerName, String accessToken) {
        OAuthProviderType provider = OAuthProviderType.from(providerName);
        return services.stream()
                .filter(s -> s.getProviderType() == provider)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 소셜 로그인입니다."))
                .getUserInfo(accessToken);
    }
}
