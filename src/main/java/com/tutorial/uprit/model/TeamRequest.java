package com.tutorial.uprit.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

/**
 * TeamRequest — students looking for teammates for hackathons/projects.
 * requiredSkills stored as comma-separated string (MVP approach).
 */
@Entity
@Table(name = "team_requests", indexes = {
        @Index(name = "idx_tr_status", columnList = "status"),
        @Index(name = "idx_tr_created_by", columnList = "created_by_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", nullable = false)
    @JsonIgnore
    private User createdBy;

    @NotBlank(message = "Title is required")
    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    /** Comma-separated skill names, e.g. "React,ML,Python" */
    @Column(nullable = false)
    private String requiredSkills;

    @Min(value = 2, message = "Team must have at least 2 members")
    @Column(nullable = false)
    private Integer maxMembers;

    /** Optional reference to an Event */
    private Long eventId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TeamRequestStatus status = TeamRequestStatus.OPEN;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
