package com.example.supabackend.dto;

public class AuthRequest {
    private String username;
    private String password;

    public AuthRequest() {}

    public String getUsername() { return username; }
    public void setUsername(String u) { username = u; }
    public String getPassword() { return password; }
    public void setPassword(String p) { password = p; }
}
