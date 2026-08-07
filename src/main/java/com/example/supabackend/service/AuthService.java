package com.example.supabackend.service;

import com.example.supabackend.dto.*;
import com.example.supabackend.model.*;
import com.example.supabackend.repository.UserRepository;
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

    public AuthService(AuthenticationManager authManager, UserRepository users,
                       PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.authManager = authManager;
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    public AuthResponse authenticate(AuthRequest req) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
        );
        var user = users.findByUsername(req.getUsername()).orElseThrow();
        var roles = user.getRoles().stream().map(Enum::name).toList();
        String token = jwtUtils.generateToken(user.getUsername(), roles);
        return new AuthResponse(token);
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
        u.getRoles().add(Role.ROLE_USER);
        users.save(u);
    }
}
