package com.tutorial.uprit.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamRequestResponse {
    private Long id;
    private Long createdById;
    private String createdByName;
    private String createdByAvatarUrl;
    private String title;
    private String description;
    private List<String> requiredSkills;
    private Integer maxMembers;
    private Integer currentMembers;
    private Long eventId;
    private String status;
    private LocalDateTime createdAt;
    private List<TeamMemberResponse> members;
}
