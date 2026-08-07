package com.example.supabackend.dto;

public class RegisterRequest {
    private String username;
    private String email;
    private String password;

    public RegisterRequest() {}

    public String getUsername() { return username; }
    public void setUsername(String u) { username = u; }
    public String getEmail() { return email; }
    public void setEmail(String e) { email = e; }
    public String getPassword() { return password; }
    public void setPassword(String p) { password = p; }
}
