package com.example.eventinformationsystembackend.controller;

import com.example.eventinformationsystembackend.dto.*;
import com.example.eventinformationsystembackend.service.EventService;
import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(path = "/events")
public class EventController {

    private final EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping(path = "/event-picture/{name}")
    public ResponseEntity<?> getEventPicture(@PathVariable("name") String name) throws IOException {
            byte[] eventPicture = eventService.getEventPicture(name);
            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(MediaType.valueOf("image/png"))
                    .body(eventPicture);
    }

    @GetMapping(path = "/{id}")
    public EventDtoResponse getEventById(@PathVariable("id") Long id) {
        return eventService.getEvent(id);
    }

    @GetMapping
    public List<EventDtoResponse> getAllEvents() {
        return eventService.getAllEvents();
    }

    @GetMapping("/concerts")
    public List<EventDtoResponse> getAllConcerts() {
        return eventService.getAllConcerts();
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

    @GetMapping("/{eventId}/attendance")
    public int getAttendancePercentageForEvent(@PathVariable("eventId") Long eventId) {
        return eventService.getAttendancePercentageForEvent(eventId);
    }

    @PostMapping
    public EventDtoResponse addEvent(@RequestPart @Valid EventDto eventDto,
                                     @RequestPart(value = "eventPicture") MultipartFile eventPicture) {
        return eventService.addEvent(eventDto, eventPicture);
    }

    @PutMapping(path = "/{eventId}")
    public EventDtoResponse updateEvent(@PathVariable("eventId") Long eventId,
                                        @RequestPart @Valid EventDto eventDto,
                                        @RequestPart(value = "eventPicture") MultipartFile eventPicture) {
        return eventService.updateEvent(eventId, eventDto, eventPicture);
    }

    @DeleteMapping(path = "/{eventId}")
    public ResponseEntity<Void> deleteEvent(@PathVariable("eventId") Long eventId) {
        eventService.deleteEvent(eventId);
        return ResponseEntity.noContent().build();
    }
}
