package com.skillswap.repository;

import com.skillswap.entity.UserSkillWant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserSkillWantRepository extends JpaRepository<UserSkillWant, Long> {

    List<UserSkillWant> findByUserId(Long userId);

    boolean existsByUserIdAndSkillId(Long userId, Long skillId);

    void deleteByUserIdAndSkillId(Long userId, Long skillId);

    // ✅ ADD THIS — Fetch join to avoid N+1 queries
    @Query("SELECT w FROM UserSkillWant w " +
            "JOIN FETCH w.user " +
            "JOIN FETCH w.skill " +
            "WHERE w.user.id = :userId")
    List<UserSkillWant> findByUserIdWithDetails(@Param("userId") Long userId);
}