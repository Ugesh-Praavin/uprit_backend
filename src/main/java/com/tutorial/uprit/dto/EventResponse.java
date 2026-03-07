package com.tutorial.uprit.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventResponse {

    private Long id;
    private String title;
    private String description;
    private String organizerName;
    private String organizerEmail;
    private String organizerContact;
    private String eventType;
    private String registrationLink;
    private LocalDateTime eventDate;
    private Long createdById;
    private String createdByName;
    private String location;
    private String imageUrl;
    private LocalDateTime createdAt;
}
