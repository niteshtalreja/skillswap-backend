package com.skillswap.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "tokens", indexes = {
        @Index(name = "idx_token_value", columnList = "token_value", unique = true),
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_token_type", columnList = "token_type"),
        @Index(name = "idx_is_revoked", columnList = "is_revoked")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Token implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    private Long id;

    @Column(name = "token_value", nullable = false, unique = true, columnDefinition = "TEXT")
    private String tokenValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "token_type", nullable = false, length = 50)
    @Builder.Default
    private TokenType tokenType = TokenType.BEARER;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "is_revoked", nullable = false)
    @Builder.Default
    private Boolean isRevoked = false;

    @Column(name = "is_expired", nullable = false)
    @Builder.Default
    private Boolean isExpired = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public enum TokenType {
        BEARER,
        REFRESH
    }

    public boolean isValid() {
        return !isExpired && !isRevoked && (expiresAt == null || expiresAt.isAfter(LocalDateTime.now()));
    }

    @Override
    public String toString() {
        return "Token{" +
                "id=" + id +
                ", tokenType=" + tokenType +
                ", expiresAt=" + expiresAt +
                ", isRevoked=" + isRevoked +
                ", isExpired=" + isExpired +
                ", user.id=" + (user != null ? user.getId() : null) +
                ", createdAt=" + createdAt +
                '}';
    }
}
