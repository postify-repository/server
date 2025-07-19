package com.example.Postify.dto;

public class SocialLoginRequest {

    private final String provider;

    public SocialLoginRequest(String provider) {
        this.provider = provider;
    }

    public String getProvider() {
        return provider;
    }
}