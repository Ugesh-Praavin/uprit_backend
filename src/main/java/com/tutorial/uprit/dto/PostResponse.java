package com.tutorial.uprit.dto;

import lombok.*;
import java.time.LocalDateTime;

/**
 * DTO for returning achievement post data — includes verification info.
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
    private String certificateUrl;
    private LocalDateTime createdAt;

    // Verification fields
    private String verificationStatus;
    private String verifiedByName;
    private LocalDateTime verifiedAt;
    private String verificationComment;

    // Engagement metrics
    private Long likeCount;
    private Long commentCount;
    private Boolean isLikedByCurrentUser;
}
