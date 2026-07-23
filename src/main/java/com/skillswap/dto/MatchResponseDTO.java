package com.skillswap.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchResponseDTO {
    private Long matchedUserId;
    private String matchedUserName;
    private String matchedUserCity;
    private String skillName;   // the skill you want that they offer
}
