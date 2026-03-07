package com.tutorial.uprit.dto;

import lombok.*;

/**
 * DTO for returning project data.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectResponse {

    private Long id;
    private String title;
    private String description;
    private Long createdById;
    private String createdByName;
    private String status;
}
