package com.skillswap.repository;

import com.skillswap.entity.Token;
import com.skillswap.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

    Optional<Token> findByTokenValue(String tokenValue);

    List<Token> findByUser(User user);

    List<Token> findByUserAndIsRevokedFalse(User user);

    List<Token> findByUserAndIsExpiredFalse(User user);

    @Query("SELECT t FROM Token t WHERE t.user = :user AND t.isRevoked = false AND t.isExpired = false")
    List<Token> findValidTokensByUser(@Param("user") User user);

    @Query("SELECT t FROM Token t WHERE t.user.id = :userId AND t.isRevoked = false AND t.isExpired = false")
    List<Token> findValidTokensByUserId(@Param("userId") Long userId);

    @Query("SELECT t FROM Token t WHERE t.expiresAt < :now AND t.isExpired = false")
    List<Token> findExpiredTokens(@Param("now") LocalDateTime now);

    int deleteByUserAndIsRevokedTrue(User user);

    int deleteByUserAndIsExpiredTrue(User user);
}
