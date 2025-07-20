package com.example.Postify.provider;

public class OAuthUserInfo {
    private final String email;
    private final String name;
    private final String providerId;
    private final OAuthProviderType provider;

    public OAuthUserInfo(String email, String name, String providerId, OAuthProviderType provider) {
        this.email = email;
        this.name = name;
        this.providerId = providerId;
        this.provider = provider;
    }

    public String getEmail() { return email; }
    public String getName() { return name; }
    public String getProviderId() { return providerId; }
    public OAuthProviderType getProvider() { return provider; }
}
