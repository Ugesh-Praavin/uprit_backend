package com.tutorial.uprit.dto;

import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO for returning achievement post data.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostResponse {

    private Long id;
    private Long userId;
    private String username;
    private String title;
    private String description;
    private String achievementType;
    private Integer xpAwarded;
    private String imageUrl;
    private LocalDateTime createdAt;
}
