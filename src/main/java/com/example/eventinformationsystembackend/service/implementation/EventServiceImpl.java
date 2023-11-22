package com.example.eventinformationsystembackend.service.implementation;

import com.example.eventinformationsystembackend.common.enums.Currency;
import com.example.eventinformationsystembackend.common.enums.EventType;
import com.example.eventinformationsystembackend.dto.EventDto;
import com.example.eventinformationsystembackend.dto.EventDtoResponse;
import com.example.eventinformationsystembackend.exception.DuplicateUniqueFieldException;
import com.example.eventinformationsystembackend.exception.InvalidEventDateException;
import com.example.eventinformationsystembackend.exception.ResourceNotFoundException;
import com.example.eventinformationsystembackend.model.*;
import com.example.eventinformationsystembackend.repository.EventRepository;
import com.example.eventinformationsystembackend.repository.OrderRepository;
import com.example.eventinformationsystembackend.service.EventService;
import org.apache.commons.io.FilenameUtils;
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

@Service
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final OrderRepository orderRepository;
    private final StorageServiceImpl storageServiceImpl;
    private final ImageServiceImpl imageServiceImpl;
    private final TicketServiceImpl ticketServiceImpl;
    private final ModelMapper modelMapper;

    @Autowired
    public EventServiceImpl(EventRepository eventRepository,
                            OrderRepository orderRepository,
                            StorageServiceImpl storageServiceImpl,
                            ImageServiceImpl imageServiceImpl,
                            @Lazy TicketServiceImpl ticketServiceImpl) {
        this.eventRepository = eventRepository;
        this.orderRepository = orderRepository;
        this.storageServiceImpl = storageServiceImpl;
        this.imageServiceImpl = imageServiceImpl;
        this.ticketServiceImpl = ticketServiceImpl;
        this.modelMapper = new ModelMapper();
    }

    @Override
    public EventDtoResponse getEvent(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(EVENT_DOES_NOT_EXIST));

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

        storageServiceImpl.createFolder(eventFolderPath);
        storageServiceImpl.savePictureToFileSystem(eventPicture, eventPicturePath);
        imageServiceImpl.createResizedImage(eventPicturePath, resizedEventPicturePath, imageExtension);

        return modelMapper.map(eventToAdd, EventDtoResponse.class);
    }

    @Override
    public byte[] getEventPicture(Long id) throws IOException {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(EVENT_DOES_NOT_EXIST));
        String eventPicturePath = event.getEventPicturePath();
        byte[] eventPicture =
                Files.readAllBytes(new File(eventPicturePath).toPath());
        return eventPicture;
    }

    @Override
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
            storageServiceImpl.renameFolder(oldEventFolderPath, newEventFolderPath);

            if (event.getEventPicturePath() != null) {
                String newEventPicturePath = newEventFolderPath + "\\" + event.getEventPictureName();
                event.setEventPicturePath(newEventPicturePath);
            }
        }

        if (eventPicture != null) {
            if (!eventPicture.isEmpty()) {
                String newEventPicturePath = newEventFolderPath + "\\" + eventPicture.getOriginalFilename();
                storageServiceImpl.savePictureToFileSystem(eventPicture, newEventPicturePath);
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

        if (eventDto.getArtists().isEmpty()) {
            event.setArtists(null);
        } else {
            event.getArtists().addAll(artists);
        }

        eventRepository.save(event);

        return modelMapper.map(event, EventDtoResponse.class);
    }

    @Override
    public void deleteEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException(EVENT_DOES_NOT_EXIST));

        String eventFolderPath = EVENTS_FOLDER_PATH + event.getName();

        storageServiceImpl.deleteFolder(eventFolderPath);

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
        int daysToAdd = 0;

        switch (type) {
            case 1 -> daysToAdd = 7;
            case 2 -> daysToAdd = 30;
            case 3 -> daysToAdd = 90;
            case 4 -> daysToAdd = 180;
            case 5 -> daysToAdd = 360;
        }

        LocalDateTime boundary = LocalDateTime.now().plusDays(daysToAdd);
        return eventRepository.countAllBetweenNowAndBoundaryDate(boundary);
    }

    @Override
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
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(EVENT_DOES_NOT_EXIST));

        return ticketServiceImpl.getSoldTicketsForEvent(event);
    }

    @Override
    public void toggleEventActivityStatus(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(EVENT_DOES_NOT_EXIST));

        event.setIsActive(!event.getIsActive());
        eventRepository.save(event);
    }
}