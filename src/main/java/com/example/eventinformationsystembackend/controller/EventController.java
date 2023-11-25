package com.example.eventinformationsystembackend.controller;

import com.example.eventinformationsystembackend.common.enums.EventType;
import com.example.eventinformationsystembackend.dto.*;
import com.example.eventinformationsystembackend.exception.RequiredPictureMissingException;
import com.example.eventinformationsystembackend.service.EventService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

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
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping("/event-picture/{id}")
    public ResponseEntity<?> getEventPicture(@PathVariable Long id) throws IOException {
            byte[] eventPicture = eventService.getEventPicture(id);
            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(MediaType.valueOf("image/png"))
                    .body(eventPicture);
    }

    @GetMapping("/{id}")
    public EventDtoResponse getEventById(@PathVariable Long id) {
        return eventService.getEvent(id);
    }

    @GetMapping
    public List<EventDtoResponse> getAllEvents(
            @RequestParam(value = "type", required = false) EventType type) {
        return eventService.getAllEvents(type);
    }

    @GetMapping("/active")
    public int getNumberOfActiveEvents() {
        return eventService.getNumberOfActiveEvents();
    }

    @GetMapping("/upcoming")
    public int getNumberOfUpcomingEvents(@RequestParam("type") int type) {
        return eventService.getNumberOfUpcomingEvents(type);
    }

    @GetMapping("/booked")
    public int getNumberOfBookedEventsInThePast(@RequestParam("type") int type) {
        return eventService.getNumberOfBookedEvents(type);
    }

    @GetMapping("/inactive")
    public int getNumberOfInactiveEvents() {
        return eventService.getNumberOfInactiveEvents();
    }

    @GetMapping("/{id}/attendance")
    public int getAttendancePercentageForEvent(@PathVariable Long id) {
        return eventService.getAttendancePercentageForEvent(id);
    }

    @PostMapping
    public EventDtoResponse addEvent(@RequestPart @Valid EventDto eventDto,
                                     @RequestPart(value = "eventPicture") MultipartFile eventPicture) {
        if (eventPicture == null) {
            throw new RequiredPictureMissingException(REQUIRED_PICTURE_IS_MISSING);
        }

        return eventService.addEvent(eventDto, eventPicture);
    }
    
    @PutMapping("/{id}")
    public EventDtoResponse updateEvent(@PathVariable Long id,
                                        @RequestPart @Valid EventDto eventDto,
                                        @RequestPart(value = "eventPicture") MultipartFile eventPicture) {
        if (eventPicture == null) {
            throw new RequiredPictureMissingException(REQUIRED_PICTURE_IS_MISSING);
        }

        return eventService.updateEvent(id, eventDto, eventPicture);
    }

    @PatchMapping("/activity-status/{id}")
    public ResponseEntity<Void> toggleEventActivityStatus(@PathVariable Long id) {
        eventService.toggleEventActivityStatus(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.ok().build();
    }
}
