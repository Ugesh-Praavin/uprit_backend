package com.tutorial.uprit.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamMemberResponse {
    private Long id;
    private Long userId;
    private String userName;
    private String avatarUrl;
    private String role;
    private LocalDateTime joinedAt;
}
