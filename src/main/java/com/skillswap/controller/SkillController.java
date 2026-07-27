package com.skillswap.controller;

import com.skillswap.dto.SkillRequestDTO;
import com.skillswap.service.SkillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SkillController {

    private final SkillService skillService;

    @GetMapping("/skills")
    public ResponseEntity<List<Skill>> getAllSkills() {
        return ResponseEntity.ok(skillService.getAllSkills());
    }

    @PostMapping("/skills/offer")
    public ResponseEntity<?> addOffer(@AuthenticationPrincipal User user,
                                       @Valid @RequestBody SkillRequestDTO request) {
        try {
            skillService.addOfferSkill(user.getId(), request.getSkillName());
            return ResponseEntity.status(HttpStatus.CREATED).body("Skill added to offer list");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/skills/want")
    public ResponseEntity<?> addWant(@AuthenticationPrincipal User user,
                                      @Valid @RequestBody SkillRequestDTO request) {
        try {
            skillService.addWantSkill(user.getId(), request.getSkillName());
            return ResponseEntity.status(HttpStatus.CREATED).body("Skill added to want list");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/skills/offer/{skillId}")
    public ResponseEntity<?> removeOffer(@AuthenticationPrincipal User user, @PathVariable Long skillId) {
        skillService.removeOfferSkill(user.getId(), skillId);
        return ResponseEntity.ok("Removed from offer list");
    }

    @DeleteMapping("/skills/want/{skillId}")
    public ResponseEntity<?> removeWant(@AuthenticationPrincipal User user, @PathVariable Long skillId) {
        skillService.removeWantSkill(user.getId(), skillId);
        return ResponseEntity.ok("Removed from want list");
    }

    @GetMapping("/skills/my-offers")
    public ResponseEntity<List<UserSkillOffer>> myOffers(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(skillService.getOffersForUser(user.getId()));
    }

    @GetMapping("/skills/my-wants")
    public ResponseEntity<List<UserSkillWant>> myWants(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(skillService.getWantsForUser(user.getId()));
    }
}
