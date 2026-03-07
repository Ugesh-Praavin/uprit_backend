package com.tutorial.uprit.controller;

import com.tutorial.uprit.dto.EventRequest;
import com.tutorial.uprit.dto.EventResponse;
import com.tutorial.uprit.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * EventController — campus event management.
 * Faculty create/update/delete. Students view.
 */
@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    /** Create event (faculty only via userId check in service) */
    @PostMapping
    public ResponseEntity<EventResponse> createEvent(
            @RequestParam Long facultyId,
            @Valid @RequestBody EventRequest request) {
        return new ResponseEntity<>(eventService.createEvent(facultyId, request), HttpStatus.CREATED);
    }

    /** Update event */
    @PutMapping("/{id}")
    public ResponseEntity<EventResponse> updateEvent(
            @PathVariable Long id,
            @Valid @RequestBody EventRequest request) {
        return ResponseEntity.ok(eventService.updateEvent(id, request));
    }

    /** Delete event */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

    /** Get all events */
    @GetMapping
    public ResponseEntity<List<EventResponse>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    /** Get upcoming events (eventDate >= now) */
    @GetMapping("/upcoming")
    public ResponseEntity<List<EventResponse>> getUpcomingEvents() {
        return ResponseEntity.ok(eventService.getUpcomingEvents());
    }
}
