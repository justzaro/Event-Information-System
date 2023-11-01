package com.example.eventinformationsystembackend.service;

import com.example.eventinformationsystembackend.dto.SupportTicketDto;
import com.example.eventinformationsystembackend.dto.SupportTicketDtoResponse;
import com.example.eventinformationsystembackend.dto.SupportTicketReplyDto;
import com.example.eventinformationsystembackend.dto.SupportTicketReplyDtoResponse;
import com.example.eventinformationsystembackend.exception.ResourceNotFoundException;
import com.example.eventinformationsystembackend.model.SupportTicket;
import com.example.eventinformationsystembackend.model.SupportTicketReply;
import com.example.eventinformationsystembackend.model.User;
import com.example.eventinformationsystembackend.repository.SupportTicketRepository;
import com.example.eventinformationsystembackend.repository.UserRepository;
import jakarta.validation.constraints.Email;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.eventinformationsystembackend.common.ExceptionMessages.SUPPORT_TICKET_NOT_EXIST;
import static com.example.eventinformationsystembackend.common.ExceptionMessages.USER_DOES_NOT_EXIST;

@Service
public class SupportTicketService {

    private final SupportTicketRepository supportTicketRepository;
    private final GenerationService generationService;
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public SupportTicketService(SupportTicketRepository supportTicketRepository,
                                UserRepository userRepository,
                                GenerationService generationService,
                                EmailService emailService) {
        this.supportTicketRepository = supportTicketRepository;
        this.userRepository = userRepository;
        this.generationService = generationService;
        this.emailService = emailService;
        this.modelMapper = new ModelMapper();
    }

    public List<SupportTicketDtoResponse> getAllSupportTickets() {
        List<SupportTicket> supportTickets = supportTicketRepository.findAll();

        return supportTickets
               .stream()
               .map(supportTicket -> modelMapper.map(supportTicket, SupportTicketDtoResponse.class))
               .collect(Collectors.toList());
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

    public SupportTicketDtoResponse createSupportTicket(SupportTicketDto supportTicketDto,
                                                        String username) {
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(USER_DOES_NOT_EXIST));

        SupportTicket supportTicket =
                modelMapper.map(supportTicketDto, SupportTicket.class);

        supportTicket.setCreatedAt(LocalDateTime.now());
        supportTicket.setUser(user);

        supportTicket = supportTicketRepository.save(supportTicket);

        String template = generationService.generateSupportTicketReceivedTemplate(supportTicket, user);
        emailService.sendSupportTicketReceivedEmail(supportTicket.getCustomerEmail(), template);

        return modelMapper.map(supportTicket, SupportTicketDtoResponse.class);
    }

    public void deleteSupportTicket(Long id) {
        SupportTicket supportTicket =
                supportTicketRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException(SUPPORT_TICKET_NOT_EXIST));

        supportTicketRepository.delete(supportTicket);
    }
}
