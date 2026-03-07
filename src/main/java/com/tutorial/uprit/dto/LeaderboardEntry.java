package com.tutorial.uprit.dto;

import lombok.*;

/**
 * DTO for leaderboard entries.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaderboardEntry {

    private Long userId;
    private String name;
    private String department;
    private Integer xp;
    private Integer level;
    private Integer rank;
}
