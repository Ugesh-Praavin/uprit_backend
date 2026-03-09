package com.tutorial.uprit.dto;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SimilarStudentResponse {
    private Long userId;
    private String name;
    private String department;
    private Integer xp;
    private Integer level;
    private String avatarUrl;
    private List<String> commonSkills;
    private Integer commonSkillCount;
}
