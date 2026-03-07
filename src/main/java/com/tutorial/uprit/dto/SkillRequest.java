package com.tutorial.uprit.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * DTO for creating/updating a skill.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillRequest {

    @NotBlank(message = "Skill name is required")
    private String skillName;
}
