package com.tutorial.uprit.dto;

import lombok.*;

/**
 * DTO for returning team membership data.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamResponse {

    private Long id;
    private Long projectId;
    private String projectTitle;
    private Long userId;
    private String userName;
}
