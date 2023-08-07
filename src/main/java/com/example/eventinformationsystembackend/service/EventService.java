package com.example.eventinformationsystembackend.service;

import com.example.eventinformationsystembackend.common.enums.Currency;
import com.example.eventinformationsystembackend.dto.ArtistDto;
import com.example.eventinformationsystembackend.dto.ArtistDtoResponse;
import com.example.eventinformationsystembackend.dto.EventDto;
import com.example.eventinformationsystembackend.dto.EventDtoResponse;
import com.example.eventinformationsystembackend.exception.DuplicateUniqueFieldException;
import com.example.eventinformationsystembackend.exception.InvalidEventDateException;
import com.example.eventinformationsystembackend.exception.ResourceNotFoundException;
import com.example.eventinformationsystembackend.model.Artist;
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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.eventinformationsystembackend.common.ExceptionMessages.*;
import static com.example.eventinformationsystembackend.common.FilePaths.EVENTS_FOLDER_PATH;
import static com.example.eventinformationsystembackend.common.FilePaths.USERS_FOLDER_PATH;

@Service
public class EventService {
    private final EventRepository eventRepository;
    private final StorageService storageService;
    private final ModelMapper modelMapper;

    @Autowired
    public EventService(EventRepository eventRepository,
                        StorageService storageService) {
        this.eventRepository = eventRepository;
        this.storageService = storageService;
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
            throw new InvalidEventDateException(START_DATE_IS_AFTER_END_DATE);
        }

        if (eventDto.getStartDate().isEqual(eventDto.getEndDate())) {
            throw new InvalidEventDateException(EQUAL_START_AND_END_DATE);
        }

        Event eventToAdd = modelMapper.map(eventDto, Event.class);

        String eventFolderPath = EVENTS_FOLDER_PATH + eventToAdd.getName();
        String eventPicturePath = eventFolderPath + "\\" + eventPicture.getOriginalFilename();

        if (eventDto.getCurrency() == null) {
            eventToAdd.setCurrency(Currency.BGN);
        }

        eventToAdd.setEventPicturePath(eventPicturePath);
        eventToAdd.setIsActive(true);

        eventRepository.save(eventToAdd);

        storageService.createFolder(eventFolderPath);
        storageService.savePictureToFileSystem(eventPicture, eventPicturePath);

        return modelMapper.map(eventToAdd, EventDtoResponse.class);
    }

    public byte[] getEventPicture(String eventName) throws IOException {
        Event event = eventRepository.findEventByName(eventName)
                .orElseThrow(() -> new ResourceNotFoundException(EVENT_DOES_NOT_EXIST));
        String eventPicturePath = event.getEventPicturePath();
        byte[] eventPicture =
                Files.readAllBytes(new File(eventPicturePath).toPath());
        return eventPicture;
    }

    public EventDtoResponse updateEvent(Long eventId, EventDto eventDto,
                                              MultipartFile eventPicture) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException(EVENT_DOES_NOT_EXIST));

        String eventFolderPath = EVENTS_FOLDER_PATH + event.getName();

        if (eventRepository.findEventByName(eventDto.getName()).isPresent()) {
            throw new DuplicateUniqueFieldException(EVENT_NAME_ALREADY_EXISTS);
        }

        event.setName(eventDto.getName());
        event.setDescription(eventDto.getDescription());
        event.setLocation(eventDto.getLocation());
        event.setStartDate(eventDto.getStartDate());
        event.setEndDate(eventDto.getEndDate());
        event.setCurrency(eventDto.getCurrency());
        event.setTicketPrice(eventDto.getTicketPrice());
        event.setCapacity(eventDto.getCapacity());

        List<Artist> artists = event.getArtists();

        List<Artist> newArtists
                = eventDto.getArtists()
                .stream()
                .map(artistDtoResponse -> modelMapper.map(artistDtoResponse, Artist.class)).toList();

        artists.clear();

        for (Artist artist : newArtists) {
            event.getArtists().add(artist);
        }

        String newEventPath = EVENTS_FOLDER_PATH + eventDto.getName();
        String newEventPicturePath = newEventPath + "\\" + eventPicture.getOriginalFilename();

        storageService.renameFolder(eventFolderPath, newEventPath);
        storageService.savePictureToFileSystem(eventPicture, newEventPicturePath);

        eventRepository.save(event);

        return modelMapper.map(event, EventDtoResponse.class);
    }

    public void deleteEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException(EVENT_DOES_NOT_EXIST));

        String eventFolderPath = EVENTS_FOLDER_PATH + event.getName();

        storageService.deleteFolder(eventFolderPath);

        eventRepository.delete(event);
    }
}
