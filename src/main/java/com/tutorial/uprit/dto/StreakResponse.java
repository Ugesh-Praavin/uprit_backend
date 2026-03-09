package com.tutorial.uprit.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StreakResponse {

    private Integer currentStreak;
    private Integer longestStreak;
    private String lastAchievementDate;
}
