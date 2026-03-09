package com.tutorial.uprit.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConnectionResponse {

    private Long id;
    private Long userId;
    private String userName;
    private String department;
    private String avatarUrl;
    private Integer xp;
    private String status;
    private LocalDateTime createdAt;
}
