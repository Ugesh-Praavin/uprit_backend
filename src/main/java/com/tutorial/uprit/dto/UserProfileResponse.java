package com.tutorial.uprit.dto;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileResponse {

    private Long id;
    private String name;
    private String email;
    private String department;
    private String avatarUrl;
    private Integer year;
    private Integer xp;
    private Integer level;
    private Integer coins;
    private String role;

    // Social counts
    private Long followersCount;
    private Long followingCount;
    private Long connectionsCount;

    // Post stats
    private Long totalPosts;
    private Long verifiedPosts;

    // Skills
    private List<String> skills;

    // Recent posts
    private List<PostResponse> recentPosts;

    // Relationship with viewer
    private Boolean isFollowing;
    private String connectionStatus; // NONE, PENDING, ACCEPTED
}
