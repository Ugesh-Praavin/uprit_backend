package com.tutorial.uprit.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * DTO for adding XP to a user.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class XpRequest {

    @NotNull(message = "XP amount is required")
    @Min(value = 1, message = "XP must be at least 1")
    private Integer xp;
}
