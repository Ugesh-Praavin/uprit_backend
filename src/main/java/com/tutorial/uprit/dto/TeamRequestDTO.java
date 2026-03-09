package com.tutorial.uprit.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamRequestDTO {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    /** Comma-separated skills, e.g. "React,ML,Python" */
    @NotBlank(message = "Required skills are needed")
    private String requiredSkills;

    @Min(value = 2, message = "Team must have at least 2 members")
    private Integer maxMembers;

    /** Optional event ID */
    private Long eventId;
}
