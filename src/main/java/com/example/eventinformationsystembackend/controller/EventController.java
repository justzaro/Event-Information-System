package com.example.eventinformationsystembackend.controller;

import com.example.eventinformationsystembackend.dto.EventDto;
import com.example.eventinformationsystembackend.dto.EventDtoResponse;
import com.example.eventinformationsystembackend.service.EventService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/events")
public class EventController {

    private final EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping(path = "/add")
    public EventDtoResponse addEvent(@Valid @RequestBody EventDto eventDto) {
        return eventService.addEvent(eventDto);
    }

    @GetMapping(path = "/{id}")
    public EventDtoResponse getEvent(@PathVariable("id") Long id) {
        return eventService.getEvent(id);
    }
}
