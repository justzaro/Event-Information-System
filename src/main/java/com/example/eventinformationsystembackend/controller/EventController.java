package com.example.eventinformationsystembackend.controller;

import com.example.eventinformationsystembackend.common.enums.EventType;
import com.example.eventinformationsystembackend.dto.*;
import com.example.eventinformationsystembackend.exception.RequiredPictureMissingException;
import com.example.eventinformationsystembackend.service.implementation.EventServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import static com.example.eventinformationsystembackend.common.ExceptionMessages.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {

    private final EventServiceImpl eventServiceImpl;

    @Autowired
    public EventController(EventServiceImpl eventServiceImpl) {
        this.eventServiceImpl = eventServiceImpl;
    }

    @GetMapping("/event-picture/{id}")
    public ResponseEntity<?> getEventPicture(@PathVariable Long id) throws IOException {
            byte[] eventPicture = eventServiceImpl.getEventPicture(id);
            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(MediaType.valueOf("image/png"))
                    .body(eventPicture);
    }

    @GetMapping("/{id}")
    public EventDtoResponse getEventById(@PathVariable Long id) {
        return eventServiceImpl.getEvent(id);
    }

    @GetMapping
    public List<EventDtoResponse> getAllEvents(
            @RequestParam(value = "type", required = false) EventType type) {
        return eventServiceImpl.getAllEvents(type);
    }

    @GetMapping("/active")
    public int getNumberOfActiveEvents() {
        return eventServiceImpl.getNumberOfActiveEvents();
    }

    @GetMapping("/upcoming")
    public int getNumberOfUpcomingEvents(@RequestParam("type") int type) {
        return eventServiceImpl.getNumberOfUpcomingEvents(type);
    }

    @GetMapping("/booked")
    public int getNumberOfBookedEventsInThePast(@RequestParam("type") int type) {
        return eventServiceImpl.getNumberOfBookedEvents(type);
    }

    @GetMapping("/inactive")
    public int getNumberOfInactiveEvents() {
        return eventServiceImpl.getNumberOfInactiveEvents();
    }

    @GetMapping("/{id}/attendance")
    public int getAttendancePercentageForEvent(@PathVariable Long id) {
        return eventServiceImpl.getAttendancePercentageForEvent(id);
    }

    @PostMapping
    public EventDtoResponse addEvent(@RequestPart @Valid EventDto eventDto,
                                     @RequestPart(value = "eventPicture") MultipartFile eventPicture) {
        if (eventPicture == null) {
            throw new RequiredPictureMissingException(REQUIRED_PICTURE_IS_MISSING);
        }

        return eventServiceImpl.addEvent(eventDto, eventPicture);
    }
    
    @PutMapping("/{id}")
    public EventDtoResponse updateEvent(@PathVariable Long id,
                                        @RequestPart @Valid EventDto eventDto,
                                        @RequestPart(value = "eventPicture") MultipartFile eventPicture) {
        if (eventPicture == null) {
            throw new RequiredPictureMissingException(REQUIRED_PICTURE_IS_MISSING);
        }

        return eventServiceImpl.updateEvent(id, eventDto, eventPicture);
    }

    @PatchMapping("/activity-status/{id}")
    public ResponseEntity<Void> toggleEventActivityStatus(@PathVariable Long id) {
        eventServiceImpl.toggleEventActivityStatus(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventServiceImpl.deleteEvent(id);
        return ResponseEntity.ok().build();
    }
}
