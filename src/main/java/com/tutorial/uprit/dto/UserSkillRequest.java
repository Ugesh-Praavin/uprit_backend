package com.tutorial.uprit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * DTO for assigning a skill to a user.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSkillRequest {

    @NotNull(message = "Skill ID is required")
    private Long skillId;

    @NotBlank(message = "Proficiency level is required")
    private String proficiencyLevel;
}
