package com.tutorial.uprit.dto;

import lombok.*;

/**
 * DTO returned after successful authentication.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    private String token;
    private String email;
    private String name;
    private String role;
}
