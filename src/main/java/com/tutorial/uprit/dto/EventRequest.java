package com.tutorial.uprit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotBlank(message = "Organizer name is required")
    private String organizerName;

    private String organizerEmail;

    private String organizerContact;

    @NotNull(message = "Event type is required")
    private String eventType;

    private String registrationLink;

    @NotNull(message = "Event date is required")
    private LocalDateTime eventDate;

    private String location;

    private String imageUrl;
}
