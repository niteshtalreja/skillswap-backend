package com.skillswap.dto;

import com.skillswap.entity.ExchangeRequest.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRequestResponseDTO {

    private Long id;
    private UserResponseDTO sender;
    private UserResponseDTO receiver;
    private SkillDTO senderSkill;
    private SkillDTO receiverSkill;
    private RequestStatus status;
    private String message;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}