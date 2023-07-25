package com.example.eventinformationsystembackend.service;

import com.example.eventinformationsystembackend.common.enums.Currency;
import com.example.eventinformationsystembackend.dto.EventDto;
import com.example.eventinformationsystembackend.dto.EventDtoResponse;
import com.example.eventinformationsystembackend.exception.ResourceNotFoundException;
import com.example.eventinformationsystembackend.model.Event;
import com.example.eventinformationsystembackend.repository.EventRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import static com.example.eventinformationsystembackend.common.ExceptionMessages.*;
@Service
public class EventService {
    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
        this.modelMapper = new ModelMapper();
    }

    public EventDtoResponse getEvent(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(EVENT_DOES_NOT_EXIST));

        return modelMapper.map(event, EventDtoResponse.class);
    }

    public EventDtoResponse addEvent(EventDto eventDto) {
        if (eventRepository.findEventByName(eventDto.getName()).isPresent()) {
            throw new IllegalStateException("event with this name already exits");
        }

        if (eventDto.getStartDate().isAfter(eventDto.getEndDate())) {
            throw new IllegalStateException("start date is after end date");
        }

        if (eventDto.getStartDate().isEqual(eventDto.getEndDate())) {
            throw new IllegalStateException("start and end date should not be equal");
        }


        Event eventToAdd = modelMapper.map(eventDto, Event.class);
        eventToAdd.setCurrency(Currency.BGN);
        eventToAdd.setEventPicturePath("asd");

        return modelMapper.map((eventRepository.save(eventToAdd)), EventDtoResponse.class);
    }
}
