package com.tutorial.uprit.dto;

import lombok.*;

/**
 * DTO for returning skill data.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillResponse {

    private Long id;
    private String skillName;
}
