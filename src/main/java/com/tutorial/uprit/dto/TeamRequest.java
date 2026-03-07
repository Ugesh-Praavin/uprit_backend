package com.tutorial.uprit.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * DTO for adding a user to a project team.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamRequest {

    @NotNull(message = "User ID is required")
    private Long userId;
}
