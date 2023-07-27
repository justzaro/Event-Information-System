package com.example.eventinformationsystembackend.service;

import com.example.eventinformationsystembackend.common.enums.Currency;
import com.example.eventinformationsystembackend.dto.EventDto;
import com.example.eventinformationsystembackend.dto.EventDtoResponse;
import com.example.eventinformationsystembackend.exception.DuplicateUniqueFieldException;
import com.example.eventinformationsystembackend.exception.ResourceNotFoundException;
import com.example.eventinformationsystembackend.model.Event;
import com.example.eventinformationsystembackend.model.User;
import com.example.eventinformationsystembackend.repository.EventRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static com.example.eventinformationsystembackend.common.ExceptionMessages.*;
import static com.example.eventinformationsystembackend.common.FilePaths.EVENTS_FOLDER_PATH;
import static com.example.eventinformationsystembackend.common.FilePaths.USERS_FOLDER_PATH;

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

    public EventDtoResponse getEvent(String name) {
        Event event = eventRepository.findEventByName(name)
                .orElseThrow(() -> new ResourceNotFoundException(EVENT_DOES_NOT_EXIST));

        return modelMapper.map(event, EventDtoResponse.class);
    }

    public EventDtoResponse addEvent(EventDto eventDto, MultipartFile eventPicture) {
        if (eventRepository.findEventByName(eventDto.getName()).isPresent()) {
            throw new DuplicateUniqueFieldException(EVENT_NAME_ALREADY_EXISTS);
        }

        if (eventDto.getStartDate().isAfter(eventDto.getEndDate())) {
            throw new IllegalStateException("start date is after end date");
        }

        if (eventDto.getStartDate().isEqual(eventDto.getEndDate())) {
            throw new IllegalStateException("start and end date should not be equal");
        }

        Event eventToAdd = modelMapper.map(eventDto, Event.class);

        String eventFolderPath = EVENTS_FOLDER_PATH + eventToAdd.getName();

        eventToAdd.setCurrency(Currency.BGN);
        eventToAdd.setEventPicturePath(eventFolderPath + "\\" + eventPicture.getOriginalFilename());

        new File(eventFolderPath).mkdirs();

        eventRepository.save(eventToAdd);

        try {
            uploadEventPictureToFileSystem(eventPicture, eventFolderPath + "\\"
                    + eventPicture.getOriginalFilename());
        } catch (IOException e) {

        }

        return modelMapper.map(eventDto, EventDtoResponse.class);
    }

    private void uploadEventPictureToFileSystem(MultipartFile eventPicture,
                                                String path) throws IOException {
        eventPicture.transferTo(new File(path));
    }

    public byte[] getEventPicture(String eventName) throws IOException {
        Event event = eventRepository.findEventByName(eventName)
                .orElseThrow(() -> new ResourceNotFoundException(EVENT_DOES_NOT_EXIST));
        String eventPicturePath = event.getEventPicturePath();
        byte[] eventPicture =
                Files.readAllBytes(new File(eventPicturePath).toPath());
        return eventPicture;
    }

}
