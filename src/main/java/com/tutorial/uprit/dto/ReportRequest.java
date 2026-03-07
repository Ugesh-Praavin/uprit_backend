package com.tutorial.uprit.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportRequest {

    @NotNull(message = "Post ID is required")
    private Long postId;

    @NotNull(message = "Reason is required")
    private String reason;

    /** Optional additional details */
    private String details;
}
