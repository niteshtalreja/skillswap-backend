package com.skillswap.repository;

import com.skillswap.entity.User;
import com.skillswap.entity.UserSkillOffer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserSkillOfferRepository extends JpaRepository<UserSkillOffer, Long> {

    List<UserSkillOffer> findByUserId(Long userId);

    boolean existsByUserIdAndSkillId(Long userId, Long skillId);

    void deleteByUserIdAndSkillId(Long userId, Long skillId);

    // ✅ ADD THIS — Fetch join to avoid N+1 queries
    @Query("SELECT o FROM UserSkillOffer o " +
            "JOIN FETCH o.user " +
            "JOIN FETCH o.skill " +
            "WHERE o.user.id = :userId")
    List<UserSkillOffer> findByUserIdWithDetails(@Param("userId") Long userId);

    // ✅ FIXED — Remove JOIN FETCH from this query
    @Query("SELECT o.user FROM UserSkillOffer o " +
            "WHERE o.skill.id = :skillId AND o.user.id <> :currentUserId")
    List<User> findUsersOfferingSkill(@Param("skillId") Long skillId,
                                      @Param("currentUserId") Long currentUserId);
}