package com.tutorial.uprit.dto;

import lombok.*;

/**
 * DTO for returning user-skill assignment data.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSkillResponse {

    private Long id;
    private Long userId;
    private String userName;
    private Long skillId;
    private String skillName;
    private String proficiencyLevel;
}
