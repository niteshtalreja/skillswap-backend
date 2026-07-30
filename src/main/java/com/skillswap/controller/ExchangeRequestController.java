package com.skillswap.controller;

import com.skillswap.dto.ExchangeRequestDTO;
import com.skillswap.dto.ExchangeRequestResponseDTO;
import com.skillswap.entity.User;
import com.skillswap.service.ExchangeRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exchange")
@RequiredArgsConstructor
public class ExchangeRequestController {

    private final ExchangeRequestService exchangeRequestService;

    @PostMapping("/request")
    public ResponseEntity<?> sendRequest(@Valid @RequestBody ExchangeRequestDTO requestDTO,
                                         @AuthenticationPrincipal User user) {
        // ✅ ADD LOGS
        System.out.println("=== SEND REQUEST ===");
        System.out.println("User ID: " + user.getId());
        System.out.println("User Email: " + user.getEmail());
        System.out.println("Receiver ID: " + requestDTO.getReceiverId());
        System.out.println("Sender Skill ID: " + requestDTO.getSenderSkillId());
        System.out.println("Receiver Skill ID: " + requestDTO.getReceiverSkillId());

        try {
            ExchangeRequestResponseDTO response = exchangeRequestService.sendRequest(requestDTO, user.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.err.println("ERROR: " + e.getMessage());  // ✅ ADD LOG
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/request/{requestId}/accept")
    public ResponseEntity<?> acceptRequest(@PathVariable Long requestId,
                                           @AuthenticationPrincipal User user) {
        try {
            ExchangeRequestResponseDTO response = exchangeRequestService.acceptRequest(requestId, user.getId());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/request/{requestId}/reject")
    public ResponseEntity<?> rejectRequest(@PathVariable Long requestId,
                                           @AuthenticationPrincipal User user) {
        try {
            ExchangeRequestResponseDTO response = exchangeRequestService.rejectRequest(requestId, user.getId());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/request/{requestId}/cancel")
    public ResponseEntity<?> cancelRequest(@PathVariable Long requestId,
                                           @AuthenticationPrincipal User user) {
        try {
            ExchangeRequestResponseDTO response = exchangeRequestService.cancelRequest(requestId, user.getId());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/my-requests/sent")
    public ResponseEntity<List<ExchangeRequestResponseDTO>> getMySentRequests(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(exchangeRequestService.getMySentRequests(user.getId()));
    }


    @GetMapping("/my-requests/received")
    public ResponseEntity<List<ExchangeRequestResponseDTO>> getMyReceivedRequests(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(exchangeRequestService.getMyReceivedRequests(user.getId()));
    }
}