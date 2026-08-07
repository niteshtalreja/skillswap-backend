package com.example.supabackend.service;

import com.example.supabackend.model.RefreshToken;
import com.example.supabackend.model.User;
import com.example.supabackend.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository repo;
    private final long refreshExpirationMs;

    public RefreshTokenService(RefreshTokenRepository repo, @Value("${app.jwt.refresh-expiration-ms}") long refreshExpirationMs) {
        this.repo = repo;
        this.refreshExpirationMs = refreshExpirationMs;
    }

    public RefreshToken createRefreshToken(User user) {
        // delete existing tokens for user
        repo.deleteByUser(user);

        RefreshToken t = new RefreshToken();
        t.setUser(user);
        t.setToken(UUID.randomUUID().toString());
        t.setExpiry(Instant.now().plusMillis(refreshExpirationMs));
        t.setRevoked(false);
        return repo.save(t);
    }

    public boolean isValid(RefreshToken token) {
        return token != null && !token.isRevoked() && token.getExpiry().isAfter(Instant.now());
    }

    public void revoke(RefreshToken token) {
        if (token != null) {
            token.setRevoked(true);
            repo.save(token);
        }
    }
}
