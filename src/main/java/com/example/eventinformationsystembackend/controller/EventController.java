package com.example.eventinformationsystembackend.controller;

import com.example.eventinformationsystembackend.dto.EventDto;
import com.example.eventinformationsystembackend.dto.EventDtoResponse;
import com.example.eventinformationsystembackend.dto.UserDto;
import com.example.eventinformationsystembackend.dto.UserDtoResponse;
import com.example.eventinformationsystembackend.service.EventService;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping(path = "/events")
public class EventController {

    private final EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping(path = "/add", consumes = { MediaType.APPLICATION_JSON_VALUE,
                                                MediaType.MULTIPART_FORM_DATA_VALUE })
    public EventDtoResponse addEvent(@Valid @RequestPart EventDto eventDto,
                                     @RequestPart("eventPicture") MultipartFile eventPicture) {
        return eventService.addEvent(eventDto, eventPicture);
    }

    @GetMapping(path = "/event-picture/{name}")
    public ResponseEntity<?> getEventPicture(@PathVariable("name") String name) throws IOException {
            byte[] eventPicture = eventService.getEventPicture(name);
            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(MediaType.valueOf("image/png"))
                    .body(eventPicture);
    }

/*    @GetMapping(path = "/{id}")
    public EventDtoResponse getEventById(@PathVariable("id") Long id) {
        return eventService.getEvent(id);
    }*/
    @GetMapping(path = "/{name}")
    public EventDtoResponse getEventByName(@PathVariable("name") String name) {
        return eventService.getEvent(name);
    }
}
