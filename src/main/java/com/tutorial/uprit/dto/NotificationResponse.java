package com.tutorial.uprit.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponse {

    private Long id;
    private String type;
    private String message;
    private Long referenceId;
    private Boolean read;
    private LocalDateTime createdAt;

    // Actor info (who performed the action)
    private Long actorId;
    private String actorName;
    private String actorAvatarUrl;
}
