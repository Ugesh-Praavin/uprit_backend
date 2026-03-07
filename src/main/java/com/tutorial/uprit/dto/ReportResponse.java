package com.tutorial.uprit.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportResponse {

    private Long id;
    private Long postId;
    private String postTitle;
    private Long facultyId;
    private String facultyName;
    private String reason;
    private String details;
    private String status;
    private LocalDateTime createdAt;
}
