package com.tutorial.uprit.service;

import com.tutorial.uprit.dto.EventRequest;
import com.tutorial.uprit.dto.EventResponse;
import com.tutorial.uprit.exception.BadRequestException;
import com.tutorial.uprit.exception.ResourceNotFoundException;
import com.tutorial.uprit.model.*;
import com.tutorial.uprit.repository.EventRepository;
import com.tutorial.uprit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Transactional
    public EventResponse createEvent(Long facultyId, EventRequest request) {
        User faculty = userRepository.findById(facultyId)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty", "id", facultyId));

        if (faculty.getRole() != Role.FACULTY && faculty.getRole() != Role.ADMIN) {
            throw new BadRequestException("Only faculty or admin can create events");
        }

        // Validate event date is in the future
        if (request.getEventDate() != null && request.getEventDate().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Event date must be in the future");
        }

        EventType eventType;
        try {
            eventType = EventType.valueOf(request.getEventType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid event type: " + request.getEventType());
        }

        Event event = Event.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .organizerName(request.getOrganizerName())
                .organizerEmail(request.getOrganizerEmail())
                .organizerContact(request.getOrganizerContact())
                .eventType(eventType)
                .registrationLink(request.getRegistrationLink())
                .eventDate(request.getEventDate())
                .createdBy(faculty)
                .location(request.getLocation())
                .imageUrl(request.getImageUrl())
                .build();

        eventRepository.save(event);
        return mapToResponse(event);
    }

    @Transactional
    public EventResponse updateEvent(Long eventId, EventRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event", "id", eventId));

        EventType eventType;
        try {
            eventType = EventType.valueOf(request.getEventType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid event type");
        }

        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setOrganizerName(request.getOrganizerName());
        event.setOrganizerEmail(request.getOrganizerEmail());
        event.setOrganizerContact(request.getOrganizerContact());
        event.setEventType(eventType);
        event.setRegistrationLink(request.getRegistrationLink());
        event.setEventDate(request.getEventDate());
        event.setLocation(request.getLocation());
        event.setImageUrl(request.getImageUrl());
        eventRepository.save(event);

        return mapToResponse(event);
    }

    public void deleteEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event", "id", eventId));
        eventRepository.delete(event);
    }

    public List<EventResponse> getAllEvents() {
        return eventRepository.findAllByOrderByEventDateAsc()
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<EventResponse> getUpcomingEvents() {
        return eventRepository.findByEventDateGreaterThanEqualOrderByEventDateAsc(LocalDateTime.now())
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    private EventResponse mapToResponse(Event event) {
        return EventResponse.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .organizerName(event.getOrganizerName())
                .organizerEmail(event.getOrganizerEmail())
                .organizerContact(event.getOrganizerContact())
                .eventType(event.getEventType().name())
                .registrationLink(event.getRegistrationLink())
                .eventDate(event.getEventDate())
                .createdById(event.getCreatedBy().getId())
                .createdByName(event.getCreatedBy().getName())
                .location(event.getLocation())
                .imageUrl(event.getImageUrl())
                .createdAt(event.getCreatedAt())
                .build();
    }
}
