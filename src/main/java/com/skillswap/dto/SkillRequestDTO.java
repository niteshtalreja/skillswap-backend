package com.skillswap.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SkillRequestDTO {

    @NotBlank(message = "Skill name is required")
    private String skillName;
}
