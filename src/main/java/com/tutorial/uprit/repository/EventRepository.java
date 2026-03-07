package com.tutorial.uprit.repository;

import com.tutorial.uprit.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    /** All events ordered by date ASC */
    List<Event> findAllByOrderByEventDateAsc();

    /** Upcoming events only — eventDate >= now */
    List<Event> findByEventDateGreaterThanEqualOrderByEventDateAsc(LocalDateTime now);
}
