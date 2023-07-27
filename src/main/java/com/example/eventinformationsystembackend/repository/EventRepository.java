package com.example.eventinformationsystembackend.repository;

import com.example.eventinformationsystembackend.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    Optional<Event> findEventByName(String name);
    Optional<Event> findEventByArtistsId(Long artistId);
}
