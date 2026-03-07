package com.tutorial.uprit.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Post entity — represents a user's achievement posted to the feed.
 * Includes verification workflow and certificate support.
 */
@Entity
@Table(name = "posts", indexes = {
        @Index(name = "idx_post_verification_status", columnList = "verificationStatus"),
        @Index(name = "idx_post_created_at", columnList = "createdAt"),
        @Index(name = "idx_post_user_id", columnList = "user_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The user who posted this achievement */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @NotBlank(message = "Title is required")
    @Column(nullable = false)
    private String title;

    @NotBlank(message = "Description is required")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AchievementType achievementType;

    /** XP awarded for this achievement — calculated by backend only */
    @Column(nullable = false)
    private Integer xpAwarded;

    /** Optional image URL for the achievement */
    private String imageUrl;

    /** Certificate URL for verification proof */
    private String certificateUrl;

    // ═══════════════════════════════════════
    // VERIFICATION FIELDS
    // ═══════════════════════════════════════

    /** Verification status — defaults to PENDING */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private VerificationStatus verificationStatus = VerificationStatus.PENDING;

    /** Faculty member who verified this post */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verified_by_id")
    @JsonIgnore
    private User verifiedBy;

    /** When the post was verified/rejected */
    private LocalDateTime verifiedAt;

    /** Faculty comment on verification */
    private String verificationComment;

    /** Auto-generated timestamp */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
