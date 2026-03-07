package com.tutorial.uprit.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Event entity — campus events created by faculty.
 * Includes organizer contact info and registration link.
 */
@Entity
@Table(name = "events", indexes = {
        @Index(name = "idx_event_date", columnList = "eventDate"),
        @Index(name = "idx_event_type", columnList = "eventType")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotBlank(message = "Organizer name is required")
    @Column(nullable = false)
    private String organizerName;

    private String organizerEmail;

    private String organizerContact;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventType eventType;

    private String registrationLink;

    @Column(nullable = false)
    private LocalDateTime eventDate;

    /** Faculty member who created this event */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", nullable = false)
    @JsonIgnore
    private User createdBy;

    private String location;

    private String imageUrl;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
