package com.tutorial.uprit.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Report entity — faculty can report posts for moderation.
 * Includes status workflow: OPEN → REVIEWED / DISMISSED.
 */
@Entity
@Table(name = "reports", indexes = {
        @Index(name = "idx_report_status", columnList = "status"),
        @Index(name = "idx_report_post_id", columnList = "post_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The reported post */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    @JsonIgnore
    private Post post;

    /** Faculty who filed the report */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "faculty_id", nullable = false)
    @JsonIgnore
    private User faculty;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportReason reason;

    /** Optional additional details */
    @Column(columnDefinition = "TEXT")
    private String details;

    /** Report moderation status */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ReportStatus status = ReportStatus.OPEN;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
