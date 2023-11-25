package com.example.eventinformationsystembackend.service.implementation;

import com.example.eventinformationsystembackend.dto.SupportTicketDto;
import com.example.eventinformationsystembackend.dto.SupportTicketDtoResponse;

import com.example.eventinformationsystembackend.model.SupportTicket;
import com.example.eventinformationsystembackend.model.User;

import com.example.eventinformationsystembackend.repository.SupportTicketRepository;

import com.example.eventinformationsystembackend.service.DataValidationService;
import com.example.eventinformationsystembackend.service.EmailService;
import com.example.eventinformationsystembackend.service.GenerationService;
import com.example.eventinformationsystembackend.service.SupportTicketService;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.eventinformationsystembackend.common.ExceptionMessages.SUPPORT_TICKET_NOT_EXIST;

@Service
@RequiredArgsConstructor
public class SupportTicketServiceImpl implements SupportTicketService {

    private final SupportTicketRepository supportTicketRepository;
    private final GenerationService generationService;
    private final EmailService emailService;
    private final DataValidationService dataValidationService;
    private final ModelMapper modelMapper = new ModelMapper();

    @Override
    public List<SupportTicketDtoResponse> getAllSupportTickets() {
        List<SupportTicket> supportTickets = supportTicketRepository.findAll();

        return supportTickets
               .stream()
               .map(supportTicket -> modelMapper.map(supportTicket, SupportTicketDtoResponse.class))
               .collect(Collectors.toList());
    }

    @Override
    public List<SupportTicketDtoResponse> getAllSupportTicketsForUser(String username) {
        User user = dataValidationService.getUserByUsername(username);

        List<SupportTicket> supportTickets =
                supportTicketRepository.findAllByUser(user);

        return supportTickets
               .stream()
               .map(supportTicket -> modelMapper.map(supportTicket, SupportTicketDtoResponse.class))
               .collect(Collectors.toList());
    }

    @Override
    public SupportTicketDtoResponse createSupportTicket(SupportTicketDto supportTicketDto,
                                                        String username) {
        User user = dataValidationService.getUserByUsername(username);

        SupportTicket supportTicket =
                modelMapper.map(supportTicketDto, SupportTicket.class);

        supportTicket.setCreatedAt(LocalDateTime.now());
        supportTicket.setUser(user);

        supportTicket = supportTicketRepository.save(supportTicket);

        String template = generationService.generateSupportTicketReceivedTemplate(supportTicket, user);
        emailService.sendSupportTicketReceivedEmail(supportTicket.getCustomerEmail(), template);

        return modelMapper.map(supportTicket, SupportTicketDtoResponse.class);
    }

    @Override
    public void deleteSupportTicket(Long id) {
        SupportTicket supportTicket = dataValidationService
                .getResourceByIdOrThrowException(id, SupportTicket.class, SUPPORT_TICKET_NOT_EXIST);

        supportTicketRepository.delete(supportTicket);
    }
}
