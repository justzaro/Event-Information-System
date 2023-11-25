package com.example.eventinformationsystembackend.service.implementation;

import com.example.eventinformationsystembackend.common.enums.EventType;

import com.example.eventinformationsystembackend.dto.EventDto;
import com.example.eventinformationsystembackend.dto.EventDtoResponse;

import com.example.eventinformationsystembackend.exception.DuplicateUniqueFieldException;
import com.example.eventinformationsystembackend.exception.InvalidEventDateException;

import com.example.eventinformationsystembackend.model.*;

import com.example.eventinformationsystembackend.repository.EventRepository;
import com.example.eventinformationsystembackend.repository.OrderRepository;

import com.example.eventinformationsystembackend.service.*;

import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.modelmapper.ModelMapper;
import org.springframework.aop.AopInvocationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.eventinformationsystembackend.common.ExceptionMessages.*;
import static com.example.eventinformationsystembackend.common.FilePaths.EVENTS_FOLDER_PATH;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final OrderRepository orderRepository;
    private final StorageService storageService;
    private final ImageService imageService;
    private final TicketService ticketService;
    private final DataValidationService dataValidationService;
    private final ModelMapper modelMapper = new ModelMapper();

    @Override
    public EventDtoResponse getEvent(Long id) {
        Event event = getEventByIdOrThrowException(id);

        return modelMapper.map(event, EventDtoResponse.class);
    }

    @Override
    public List<EventDtoResponse> getAllEvents(EventType type) {
        List<Event> allEvents;

        if (type == null) {
            allEvents = eventRepository.findAll();
        } else {
            allEvents = eventRepository.findAllByEventType(type);
        }

        return allEvents
               .stream()
               .map(event -> modelMapper.map(event, EventDtoResponse.class))
               .collect(Collectors.toList());
    }

    @Override
    public EventDtoResponse addEvent(EventDto eventDto, MultipartFile eventPicture) {
        validateEventDtoData(eventDto);

        Event event = modelMapper.map(eventDto, Event.class);
        setNewEventFolderAndPicture(event, eventPicture);

        eventRepository.save(event);
        return modelMapper.map(event, EventDtoResponse.class);
    }

    @Override
    public byte[] getEventPicture(Long id) throws IOException {
        Event event = getEventByIdOrThrowException(id);
        String eventPicturePath = event.getEventPicturePath();
        return Files.readAllBytes(new File(eventPicturePath).toPath());
    }

    @Override
    public EventDtoResponse updateEvent(Long id, EventDto eventDto,
                                        MultipartFile eventPicture) {
        Event event = getEventByIdOrThrowException(id);

        validateEventDtoData(eventDto);

        String oldEventFolderPath = EVENTS_FOLDER_PATH + event.getName();

        checkForNewEventNameAndPicture(event, eventDto, oldEventFolderPath,
                                       eventPicture);
        updateCurrentEventDetails(event, eventDto);

        eventRepository.save(event);
        return modelMapper.map(event, EventDtoResponse.class);
    }

    @Override
    public void deleteEvent(Long id) {
        Event event = getEventByIdOrThrowException(id);

        String eventFolderPath = EVENTS_FOLDER_PATH + event.getName();

        storageService.deleteFolder(eventFolderPath);
        eventRepository.delete(event);
    }

    @Override
    public boolean checkIfEventHasEnoughSeats(Event event, int ticketQuantity) {
        int ticketsBoughtForCurrentConcert;

        //This exception is thrown if `getTicketsBoughtForEvent()` method returns null
        //This happens if there are no orders for the specified concert
        try {
            ticketsBoughtForCurrentConcert = orderRepository.getTicketsBoughtForEvent(event.getId());
        } catch (AopInvocationException e) {
            return ticketQuantity <= event.getCapacity();
        }

        int eventCapacity = event.getCapacity();
        int availableTickets = eventCapacity - ticketsBoughtForCurrentConcert;

        return ticketQuantity <= eventCapacity && ticketQuantity <= availableTickets;
    }

    @Override
    public int getNumberOfUpcomingEvents(int type) {
        LocalDateTime boundary = LocalDateTime.now().plusDays(getDays(type));
        return eventRepository.countAllBetweenNowAndBoundaryDate(boundary);
    }

    @Override
    public int getNumberOfBookedEvents(int type) {
        LocalDateTime boundary = LocalDateTime.now().minusDays(getDays(type));
        return eventRepository.countAllWithAtLeastOneTicketBoughtInTheLastTargetDays(boundary);
    }

    @Override
    public int getNumberOfActiveEvents() {
        return eventRepository.countAllByIsActive(true);
    }

    @Override
    public int getNumberOfInactiveEvents() {
        return eventRepository.countAllByIsActive(false);
    }

    @Override
    public int getAttendancePercentageForEvent(Long id) {
        Event event = getEventByIdOrThrowException(id);
        return ticketService.getSoldTicketsForEvent(event);
    }

    @Override
    public void toggleEventActivityStatus(Long id) {
        Event event = getEventByIdOrThrowException(id);
        event.setIsActive(!event.getIsActive());
        eventRepository.save(event);
    }

    private Event getEventByIdOrThrowException(Long id) {
        return dataValidationService.
                getResourceByIdOrThrowException(id, Event.class, EVENT_DOES_NOT_EXIST);
    }

    private void validateEventDtoData(EventDto eventDto) {
        if (eventRepository.findEventByName(eventDto.getName()).isPresent()) {
            throw new DuplicateUniqueFieldException(EVENT_NAME_ALREADY_EXISTS);
        }

        if (eventDto.getStartDate().isAfter(eventDto.getEndDate())) {
            throw new InvalidEventDateException(START_DATE_IS_AFTER_END_DATE);
        }

        if (eventDto.getStartDate().isEqual(eventDto.getEndDate())) {
            throw new InvalidEventDateException(EQUAL_START_AND_END_DATE);
        }
    }

    private void checkForNewEventNameAndPicture(Event event, EventDto eventDto,
                                                String oldEventFolderPath,
                                                MultipartFile eventPicture) {
        String newEventFolderPath = oldEventFolderPath;

        if (!event.getName().equals(eventDto.getName())) {
            if (eventRepository.findEventByName(eventDto.getName()).isPresent()) {
                throw new DuplicateUniqueFieldException(EVENT_NAME_ALREADY_EXISTS);
            }
            newEventFolderPath = EVENTS_FOLDER_PATH + eventDto.getName();
            storageService.renameFolder(oldEventFolderPath, newEventFolderPath);

            if (event.getEventPicturePath() != null) {
                String newEventPicturePath = newEventFolderPath + "\\" + event.getEventPictureName();
                event.setEventPicturePath(newEventPicturePath);
            }
        }

        replaceOldEventPicture(event, newEventFolderPath, eventPicture);
    }

    private void replaceOldEventPicture(Event event,
                                        String newEventFolderPath,
                                        MultipartFile eventPicture) {
        if (eventPicture != null) {
            if (!eventPicture.isEmpty()) {
                String newEventPicturePath = newEventFolderPath + "\\" + eventPicture.getOriginalFilename();
                storageService.savePictureToFileSystem(eventPicture, newEventPicturePath);
                event.setEventPicturePath(newEventPicturePath);
            }
        }
    }

    private void updateCurrentEventDetails(Event event, EventDto eventDto) {
        modelMapper.map(eventDto, event);

        Set<Artist> artists
                = eventDto.getArtists()
                .stream()
                .map(artistDtoResponse -> modelMapper.map(artistDtoResponse, Artist.class))
                .collect(Collectors.toSet());

        if (eventDto.getArtists().isEmpty()) {
            event.setArtists(null);
        } else {
            event.getArtists().addAll(artists);
        }
    }

    private void setNewEventFolderAndPicture(Event event, MultipartFile eventPicture) {
        String eventFolderPath = EVENTS_FOLDER_PATH + event.getName();
        String eventPicturePath = eventFolderPath + "\\" + eventPicture.getOriginalFilename();
        String resizedEventPicturePath = eventFolderPath + "\\resized.jpg";

        String imageExtension = FilenameUtils.getExtension(eventPicture.getOriginalFilename());

        event.setEventPicturePath(eventPicturePath);
        event.setEventPictureName(eventPicture.getOriginalFilename());

        storageService.createFolder(eventFolderPath);
        storageService.savePictureToFileSystem(eventPicture, eventPicturePath);
        imageService.createResizedImage(eventPicturePath, resizedEventPicturePath, imageExtension);
    }

    private int getDays(int type) {
        return switch (type) {
            case 1 -> 7;
            case 2 -> 30;
            case 3 -> 90;
            case 4 -> 180;
            case 5 -> 360;
            default -> 0;
        };
    }
}