package com.tutorial.uprit.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Connection entity — mutual relationship (like LinkedIn).
 * Single row represents both sides: if A→B is ACCEPTED, both are connected.
 * Unique constraint on (requester, receiver) + service checks reverse
 * direction.
 */
@Entity
@Table(name = "connections", uniqueConstraints = @UniqueConstraint(name = "uk_connection_pair", columnNames = {
        "requester_id", "receiver_id" }), indexes = {
                @Index(name = "idx_conn_status", columnList = "status"),
                @Index(name = "idx_conn_requester", columnList = "requester_id"),
                @Index(name = "idx_conn_receiver", columnList = "receiver_id")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Connection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    @JsonIgnore
    private User requester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    @JsonIgnore
    private User receiver;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ConnectionStatus status = ConnectionStatus.PENDING;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
