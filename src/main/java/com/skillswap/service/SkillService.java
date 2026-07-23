package com.skillswap.service;

import com.skillswap.entity.Skill;
import com.skillswap.entity.User;
import com.skillswap.entity.UserSkillOffer;
import com.skillswap.entity.UserSkillWant;
import com.skillswap.repository.SkillRepository;
import com.skillswap.repository.UserRepository;
import com.skillswap.repository.UserSkillOfferRepository;
import com.skillswap.repository.UserSkillWantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final UserSkillOfferRepository offerRepository;
    private final UserSkillWantRepository wantRepository;

    public List<Skill> getAllSkills() {
        return skillRepository.findAll();
    }

    // Finds an existing skill by name (case-insensitive) or creates a new one
    private Skill getOrCreateSkill(String name) {
        return skillRepository.findByNameIgnoreCase(name.trim())
                .orElseGet(() -> skillRepository.save(new Skill(null, name.trim())));
    }

    public void addOfferSkill(Long userId, String skillName) {
        User user = getUser(userId);
        Skill skill = getOrCreateSkill(skillName);

        if (offerRepository.existsByUserIdAndSkillId(userId, skill.getId())) {
            throw new IllegalArgumentException("Skill already added to your offer list");
        }
        offerRepository.save(new UserSkillOffer(null, user, skill));
    }

    public void addWantSkill(Long userId, String skillName) {
        User user = getUser(userId);
        Skill skill = getOrCreateSkill(skillName);

        if (wantRepository.existsByUserIdAndSkillId(userId, skill.getId())) {
            throw new IllegalArgumentException("Skill already added to your want list");
        }
        wantRepository.save(new UserSkillWant(null, user, skill));
    }

    public void removeOfferSkill(Long userId, Long skillId) {
        offerRepository.deleteByUserIdAndSkillId(userId, skillId);
    }

    public void removeWantSkill(Long userId, Long skillId) {
        wantRepository.deleteByUserIdAndSkillId(userId, skillId);
    }

    public List<UserSkillOffer> getOffersForUser(Long userId) {
        return offerRepository.findByUserId(userId);
    }

    public List<UserSkillWant> getWantsForUser(Long userId) {
        return wantRepository.findByUserId(userId);
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}
