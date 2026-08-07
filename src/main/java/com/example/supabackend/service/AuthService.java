package com.example.supabackend.service;

import com.example.supabackend.dto.*;
import com.example.supabackend.model.*;
import com.example.supabackend.repository.*;
import com.example.supabackend.security.JwtUtils;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AuthService {

    private final AuthenticationManager authManager;
    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final RoleRepository roleRepo;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepository refreshTokenRepo;

    public AuthService(AuthenticationManager authManager, UserRepository users,
                       PasswordEncoder passwordEncoder, JwtUtils jwtUtils,
                       RoleRepository roleRepo, RefreshTokenService refreshTokenService,
                       RefreshTokenRepository refreshTokenRepo) {
        this.authManager = authManager;
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.roleRepo = roleRepo;
        this.refreshTokenService = refreshTokenService;
        this.refreshTokenRepo = refreshTokenRepo;
    }

    public AuthResponse authenticate(AuthRequest req) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
        );
        var user = users.findByUsername(req.getUsername()).orElseThrow();
        var roles = user.getRoles().stream().map(Role::getName).toList();
        String token = jwtUtils.generateToken(user.getUsername(), roles);
        var refreshToken = refreshTokenService.createRefreshToken(user);
        AuthResponse r = new AuthResponse(token);
        r.setRefreshToken(refreshToken.getToken());
        return r;
    }

    public void register(RegisterRequest req) {
        if (users.existsByUsername(req.getUsername())) {
            throw new IllegalArgumentException("Username already taken");
        }
        if (users.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("Email already used");
        }
        User u = new User();
        u.setUsername(req.getUsername());
        u.setEmail(req.getEmail());
        u.setPassword(passwordEncoder.encode(req.getPassword()));
        Role userRole = roleRepo.findByName("ROLE_USER").orElseThrow(() -> new IllegalStateException("Default role not found"));
        u.getRoles().add(userRole);
        users.save(u);
    }

    public AuthResponse refreshToken(String refreshTokenStr) {
        var opt = refreshTokenRepo.findByToken(refreshTokenStr);
        if (opt.isEmpty()) throw new IllegalArgumentException("Invalid refresh token");
        var token = opt.get();
        if (!refreshTokenService.isValid(token)) throw new IllegalArgumentException("Refresh token expired or revoked");
        var user = token.getUser();
        var roles = user.getRoles().stream().map(Role::getName).toList();
        String newAccessToken = jwtUtils.generateToken(user.getUsername(), roles);
        // optionally issue a new refresh token
        refreshTokenService.revoke(token);
        var newRefresh = refreshTokenService.createRefreshToken(user);
        AuthResponse r = new AuthResponse(newAccessToken);
        r.setRefreshToken(newRefresh.getToken());
        return r;
    }

    public void logout(String refreshTokenStr) {
        var opt = refreshTokenRepo.findByToken(refreshTokenStr);
        opt.ifPresent(t -> {
            refreshTokenService.revoke(t);
        });
    }
}
