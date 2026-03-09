package com.tutorial.uprit.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FollowResponse {

    private Long id;
    private Long userId;
    private String userName;
    private String department;
    private String avatarUrl;
    private Integer xp;
    private Integer level;
}
