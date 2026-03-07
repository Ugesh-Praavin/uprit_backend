package com.tutorial.uprit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * DTO for creating an achievement post.
 * XP is NEVER accepted from client — calculated by backend.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Achievement type is required")
    private String achievementType;

    /** Optional image URL */
    private String imageUrl;

    /** Certificate URL for verification proof */
    private String certificateUrl;
}
