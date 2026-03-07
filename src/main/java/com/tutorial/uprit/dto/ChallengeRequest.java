package com.tutorial.uprit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO for creating/updating a challenge.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChallengeRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "XP reward is required")
    private Integer xpReward;

    private LocalDateTime deadline;
}
