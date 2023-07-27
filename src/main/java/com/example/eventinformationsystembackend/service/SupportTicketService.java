package com.example.eventinformationsystembackend.service;

import com.example.eventinformationsystembackend.dto.SupportTicketDto;
import com.example.eventinformationsystembackend.dto.SupportTicketDtoResponse;
import com.example.eventinformationsystembackend.exception.ResourceNotFoundException;
import com.example.eventinformationsystembackend.model.SupportTicket;
import com.example.eventinformationsystembackend.model.User;
import com.example.eventinformationsystembackend.repository.SupportTicketRepository;
import com.example.eventinformationsystembackend.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.eventinformationsystembackend.common.ExceptionMessages.USER_DOES_NOT_EXIST;

@Service
public class SupportTicketService {

    private final SupportTicketRepository supportTicketRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public SupportTicketService(SupportTicketRepository supportTicketRepository,
                                UserRepository userRepository) {
        this.supportTicketRepository = supportTicketRepository;
        this.userRepository = userRepository;
        this.modelMapper = new ModelMapper();
    }

    public SupportTicketDtoResponse createSupportTicket(SupportTicketDto supportTicketDto,
                                                        String username) {
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(USER_DOES_NOT_EXIST));

        SupportTicket supportTicket =
                modelMapper.map(supportTicketDto, SupportTicket.class);

        supportTicket.setCreatedAt(LocalDateTime.now());
        supportTicket.setUser(user);

        supportTicketRepository.save(supportTicket);

        return modelMapper.map(supportTicket, SupportTicketDtoResponse.class);
    }

    public List<SupportTicketDtoResponse> getAllSupportTicketsForUser(String username) {
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(USER_DOES_NOT_EXIST));

        List<SupportTicket> supportTickets =
                supportTicketRepository.findAllByUser(user);

        //add exception for is empty list?

        return supportTickets
               .stream()
               .map(supportTicket -> modelMapper.map(supportTicket, SupportTicketDtoResponse.class))
               .collect(Collectors.toList());
    }
}
