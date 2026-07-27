package com.skillswap.service;

import com.skillswap.dto.MatchResponseDTO;
import com.skillswap.repository.UserSkillOfferRepository;
import com.skillswap.repository.UserSkillWantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final UserSkillWantRepository wantRepository;
    private final UserSkillOfferRepository offerRepository;

    /**
     * Core matching logic:
     * For every skill the current user WANTS, find other users who OFFER that skill.
     */
    public List<MatchResponseDTO> findMatchesForUser(Long userId) {
        List<UserSkillWant> myWants = wantRepository.findByUserId(userId);
        List<MatchResponseDTO> matches = new ArrayList<>();

        for (UserSkillWant want : myWants) {
            List<User> usersOffering = offerRepository.findUsersOfferingSkill(want.getSkill().getId(), userId);

            for (User matchedUser : usersOffering) {
                matches.add(new MatchResponseDTO(
                        matchedUser.getId(),
                        matchedUser.getName(),
                        matchedUser.getCity(),
                        want.getSkill().getName()
                ));
            }
        }

        return matches;
    }
}
