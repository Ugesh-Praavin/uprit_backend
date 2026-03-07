package com.tutorial.uprit.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * User entity — core entity representing a student on the platform.
 * Tracks XP, level, coins, department, and role.
 */
@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_user_department", columnList = "department"),
        @Index(name = "idx_user_year", columnList = "year"),
        @Index(name = "idx_user_xp", columnList = "xp"),
        @Index(name = "idx_user_role", columnList = "role")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Password is required")
    @Column(nullable = false)
    @JsonIgnore
    private String password;

    private String department;

    private Integer year;

    @Builder.Default
    private Integer xp = 0;

    @Builder.Default
    private Integer level = 0;

    @Builder.Default
    private Integer coins = 0;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false)
    private Role role = Role.USER;

    /** Skills associated with this user (via UserSkill join entity) */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @Builder.Default
    private List<UserSkill> userSkills = new ArrayList<>();

    /** Projects created by this user */
    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @Builder.Default
    private List<Project> projects = new ArrayList<>();

    /** Teams this user belongs to */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @Builder.Default
    private List<Team> teams = new ArrayList<>();
}
