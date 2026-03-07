package com.tutorial.uprit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerifyPostRequest {

    @NotNull(message = "Status is required (VERIFIED or REJECTED)")
    private String status;

    /** Optional comment from faculty */
    private String comment;
}
