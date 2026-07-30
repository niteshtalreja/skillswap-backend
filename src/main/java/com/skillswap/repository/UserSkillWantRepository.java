package com.skillswap.repository;

import com.skillswap.entity.UserSkillWant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserSkillWantRepository extends JpaRepository<UserSkillWant, Long> {

    List<UserSkillWant> findByUserId(Long userId);

    boolean existsByUserIdAndSkillId(Long userId, Long skillId);

    void deleteByUserIdAndSkillId(Long userId, Long skillId);
}
