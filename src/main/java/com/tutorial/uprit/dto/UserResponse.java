package com.tutorial.uprit.dto;

import lombok.*;

/**
 * DTO for returning user data without sensitive fields.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private Long id;
    private String name;
    private String email;
    private String department;
    private Integer year;
    private Integer xp;
    private Integer level;
    private Integer coins;
    private String role;
}
