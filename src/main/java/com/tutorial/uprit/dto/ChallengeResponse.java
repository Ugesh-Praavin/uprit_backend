package com.tutorial.uprit.dto;

import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO for returning challenge data.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChallengeResponse {

    private Long id;
    private String title;
    private String description;
    private Integer xpReward;
    private LocalDateTime deadline;
}
