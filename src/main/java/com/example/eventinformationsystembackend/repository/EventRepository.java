package com.example.eventinformationsystembackend.repository;

import com.example.eventinformationsystembackend.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    Optional<Event> findEventByName(String name);
    @Query(value = "UPDATE events SET is_active = false WHERE event_id = :eventId",
            nativeQuery = true)
    void updateIsActiveColumn(@Param("eventId") Long eventId);
}
