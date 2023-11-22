package com.example.eventinformationsystembackend.service;

import com.example.eventinformationsystembackend.common.enums.EventType;
import com.example.eventinformationsystembackend.dto.EventDto;
import com.example.eventinformationsystembackend.dto.EventDtoResponse;
import com.example.eventinformationsystembackend.model.Event;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface EventService {

    void deleteEvent(Long eventId);

    boolean checkIfEventHasEnoughSeats(Event event, int ticketQuantity);

    int getNumberOfUpcomingEvents(int type);

    int getNumberOfBookedEvents(int type);

    int getNumberOfActiveEvents();

    int getNumberOfInactiveEvents();

    int getAttendancePercentageForEvent(Long id);

    void toggleEventActivityStatus(Long id);

    byte[] getEventPicture(Long id) throws IOException;

    EventDtoResponse getEvent(Long id);

    EventDtoResponse addEvent(EventDto eventDto, MultipartFile eventPicture);

    EventDtoResponse updateEvent(Long eventId, EventDto eventDto, MultipartFile eventPicture);

    List<EventDtoResponse> getAllEvents(EventType type);
}
