package com.example.supabackend.dto;

public class AuthResponse {
    private String token;
    private String tokenType = "Bearer";
    private String refreshToken;

    public AuthResponse() {}
    public AuthResponse(String token) { this.token = token; }

    public String getToken() { return token; }
    public void setToken(String t) { token = t; }
    public String getTokenType() { return tokenType; }
    public void setTokenType(String t) { tokenType = t; }
    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
}
