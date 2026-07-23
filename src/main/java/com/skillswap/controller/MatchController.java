package com.skillswap.controller;

import com.skillswap.dto.MatchResponseDTO;
import com.skillswap.entity.User;
import com.skillswap.service.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/matches")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

    @GetMapping
    public ResponseEntity<List<MatchResponseDTO>> getMyMatches(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(matchService.findMatchesForUser(user.getId()));
    }
}
