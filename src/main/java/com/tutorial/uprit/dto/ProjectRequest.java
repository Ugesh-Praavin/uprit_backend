package com.tutorial.uprit.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * DTO for creating/updating a project.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    /** Optional: OPEN, IN_PROGRESS, COMPLETED */
    private String status;
}
