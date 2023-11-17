package com.example.eventinformationsystembackend.service;

import com.example.eventinformationsystembackend.common.enums.Currency;
import com.example.eventinformationsystembackend.common.enums.EventType;
import com.example.eventinformationsystembackend.dto.ArtistDto;
import com.example.eventinformationsystembackend.dto.ArtistDtoResponse;
import com.example.eventinformationsystembackend.dto.EventDto;
import com.example.eventinformationsystembackend.dto.EventDtoResponse;
import com.example.eventinformationsystembackend.exception.DuplicateUniqueFieldException;
import com.example.eventinformationsystembackend.exception.InvalidEventDateException;
import com.example.eventinformationsystembackend.exception.ResourceNotFoundException;
import com.example.eventinformationsystembackend.model.*;
import com.example.eventinformationsystembackend.repository.EventRepository;
import com.example.eventinformationsystembackend.repository.OrderRepository;
import lombok.AllArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.modelmapper.ModelMapper;
import org.springframework.aop.AopInvocationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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
import static com.example.eventinformationsystembackend.common.FilePaths.USERS_FOLDER_PATH;

@Service
public class EventService {
    private final EventRepository eventRepository;
    private final OrderRepository orderRepository;
    private final StorageService storageService;
    private final ImageService imageService;
    private final TicketService ticketService;
    private final ModelMapper modelMapper;

    @Autowired
    public EventService(EventRepository eventRepository,
                        OrderRepository orderRepository,
                        StorageService storageService,
                        ImageService imageService,
                        @Lazy TicketService ticketService) {
        this.eventRepository = eventRepository;
        this.orderRepository = orderRepository;
        this.storageService = storageService;
        this.imageService = imageService;
        this.ticketService = ticketService;
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

    public List<EventDtoResponse> getAllConcerts() {
        List<Event> allEvents = eventRepository.findAllByEventType(EventType.CONCERT);

        return allEvents
                .stream()
                .map(event -> modelMapper.map(event, EventDtoResponse.class))
                .collect(Collectors.toList());
    }

    public List<EventDtoResponse> getAllEventsByType(EventType type) {
        List<Event> allEvents = eventRepository.findAllByEventType(type);

        return allEvents
                .stream()
                .map(event -> modelMapper.map(event, EventDtoResponse.class))
                .collect(Collectors.toList());
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

        if (eventPicture == null) {
            System.out.println("asddsa");
        }

        Event eventToAdd = modelMapper.map(eventDto, Event.class);

        String eventFolderPath = EVENTS_FOLDER_PATH + eventToAdd.getName();
        String eventPicturePath = eventFolderPath + "\\" + eventPicture.getOriginalFilename();
        String resizedEventPicturePath = eventFolderPath + "\\resized.jpg";

        String imageExtension = FilenameUtils.getExtension(eventPicture.getOriginalFilename());
        System.out.println(imageExtension);

        if (eventDto.getCurrency() == null) {
            eventToAdd.setCurrency(Currency.BGN);
        }

        eventToAdd.setEventPicturePath(eventPicturePath);
        eventToAdd.setIsActive(eventDto.getIsActive());
        eventToAdd.setEventPictureName(eventPicture.getOriginalFilename());
        eventToAdd.setEventType(eventDto.getEventType());

        eventRepository.save(eventToAdd);

        storageService.createFolder(eventFolderPath);
        storageService.savePictureToFileSystem(eventPicture, eventPicturePath);
        imageService.createResizedImage(eventPicturePath, resizedEventPicturePath, imageExtension);

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

        if (!event.getName().equals(eventDto.getName())) {
            if (eventRepository.findEventByName(event.getName()).isPresent()) {
                throw new DuplicateUniqueFieldException(EVENT_NAME_ALREADY_EXISTS);
            }
        }

        if (eventDto.getStartDate().isAfter(eventDto.getEndDate())) {
            throw new InvalidEventDateException(START_DATE_IS_AFTER_END_DATE);
        }

        if (eventDto.getStartDate().isEqual(eventDto.getEndDate())) {
            throw new InvalidEventDateException(EQUAL_START_AND_END_DATE);
        }

        String oldEventFolderPath = EVENTS_FOLDER_PATH + event.getName();
        String newEventFolderPath = EVENTS_FOLDER_PATH + event.getName();

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

        if (eventPicture != null) {
            if (!eventPicture.isEmpty()) {
                String newEventPicturePath = newEventFolderPath + "\\" + eventPicture.getOriginalFilename();
                storageService.savePictureToFileSystem(eventPicture, newEventPicturePath);
                event.setEventPicturePath(newEventPicturePath);
            }
        }

        event.setName(eventDto.getName());
        event.setDescription(eventDto.getDescription());
        event.setLocation(eventDto.getLocation());
        event.setStartDate(eventDto.getStartDate());
        event.setEndDate(eventDto.getEndDate());
        event.setCurrency(eventDto.getCurrency());
        event.setTicketPrice(eventDto.getTicketPrice());
        event.setCapacity(eventDto.getCapacity());
        event.setEventType(eventDto.getEventType());
        event.setIsActive(eventDto.getIsActive());

        Set<Artist> artists = eventDto.getArtists()
                .stream()
                .map(artistDtoResponse -> modelMapper.map(artistDtoResponse, Artist.class)).collect(Collectors.toSet());
        //todo add exception if eventDto.getArtists() is empty
        if (eventDto.getArtists().isEmpty()) {
            event.setArtists(null);
        } else {
            event.getArtists().addAll(artists);
        }

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

    public boolean checkIfEventHasEnoughSeats(Event event, int ticketQuantity) {
        int ticketsBoughtForCurrentConcert;

        //This exception is thrown if `getTicketsBoughtForEvent()` method returns null
        //This happens if there are no orders for the specified concert
        try {
            ticketsBoughtForCurrentConcert = orderRepository.getTicketsBoughtForEvent(event.getId());
        } catch (AopInvocationException e) {
            System.out.println("asd");
            return ticketQuantity <= event.getCapacity();
        }

        int eventCapacity = event.getCapacity();
        int availableTickets = eventCapacity - ticketsBoughtForCurrentConcert;
        System.out.println(eventCapacity);
        System.out.println(availableTickets);
        System.out.println(ticketQuantity);
        return ticketQuantity <= eventCapacity && ticketQuantity <= availableTickets;
    }

    public int getNumberOfUpcomingEvents(int type) {
        int daysToAdd = 0;

        switch (type) {
            case 1 -> daysToAdd = 7;
            case 2 -> daysToAdd = 30;
            case 3 -> daysToAdd = 90;
            case 4 -> daysToAdd = 180;
            case 5 -> daysToAdd = 360;
        }

        LocalDateTime boundary = LocalDateTime.now().plusDays(daysToAdd);
        System.out.println(boundary);
        return eventRepository.countAllBetweenNowAndBoundaryDate(boundary);
    }

    public int getNumberOfBookedEvents(int type) {
        int daysToRemove = 0;

        switch (type) {
            case 1 -> daysToRemove = 7;
            case 2 -> daysToRemove = 30;
            case 3 -> daysToRemove = 90;
            case 4 -> daysToRemove = 180;
            case 5 -> daysToRemove = 360;
        }

        LocalDateTime boundary = LocalDateTime.now().minusDays(daysToRemove);
        System.out.println("booked" + boundary);
        return eventRepository.countAllWithAtLeastOneTicketBoughtInTheLastTargetDays(boundary);
    }

    public int getNumberOfActiveEvents() {
        return eventRepository.countAllByIsActive(true);
    }

    public int getNumberOfInactiveEvents() {
        return eventRepository.countAllByIsActive(false);
    }

    public int getAttendancePercentageForEvent(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(EVENT_DOES_NOT_EXIST));

        return ticketService.getSoldTicketsForEvent(event);
    }

    public void toggleEventActivityStatus(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(EVENT_DOES_NOT_EXIST));

        event.setIsActive(!event.getIsActive());
        eventRepository.save(event);
    }
}