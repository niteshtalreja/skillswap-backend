package com.skillswap.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRequestDTO {

    @NotNull(message = "Receiver ID is required")
    private Long receiverId;

    @NotNull(message = "Sender skill ID is required")
    private Long senderSkillId;

    @NotNull(message = "Receiver skill ID is required")
    private Long receiverSkillId;

    private String message;
}