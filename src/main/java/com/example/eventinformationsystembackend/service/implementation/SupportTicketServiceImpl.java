package com.example.eventinformationsystembackend.service.implementation;

import com.example.eventinformationsystembackend.dto.SupportTicketDto;
import com.example.eventinformationsystembackend.dto.SupportTicketDtoResponse;
import com.example.eventinformationsystembackend.exception.ResourceNotFoundException;
import com.example.eventinformationsystembackend.model.SupportTicket;
import com.example.eventinformationsystembackend.model.User;
import com.example.eventinformationsystembackend.repository.SupportTicketRepository;
import com.example.eventinformationsystembackend.repository.UserRepository;
import com.example.eventinformationsystembackend.service.SupportTicketService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.eventinformationsystembackend.common.ExceptionMessages.SUPPORT_TICKET_NOT_EXIST;
import static com.example.eventinformationsystembackend.common.ExceptionMessages.USER_DOES_NOT_EXIST;

@Service
public class SupportTicketServiceImpl implements SupportTicketService {

    private final SupportTicketRepository supportTicketRepository;
    private final GenerationServiceImpl generationServiceImpl;
    private final EmailServiceImpl emailServiceImpl;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public SupportTicketServiceImpl(SupportTicketRepository supportTicketRepository,
                                    UserRepository userRepository,
                                    GenerationServiceImpl generationServiceImpl,
                                    EmailServiceImpl emailServiceImpl) {
        this.supportTicketRepository = supportTicketRepository;
        this.userRepository = userRepository;
        this.generationServiceImpl = generationServiceImpl;
        this.emailServiceImpl = emailServiceImpl;
        this.modelMapper = new ModelMapper();
    }

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
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(USER_DOES_NOT_EXIST));

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
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(USER_DOES_NOT_EXIST));

        SupportTicket supportTicket =
                modelMapper.map(supportTicketDto, SupportTicket.class);

        supportTicket.setCreatedAt(LocalDateTime.now());
        supportTicket.setUser(user);

        supportTicket = supportTicketRepository.save(supportTicket);

        String template = generationServiceImpl.generateSupportTicketReceivedTemplate(supportTicket, user);
        emailServiceImpl.sendSupportTicketReceivedEmail(supportTicket.getCustomerEmail(), template);

        return modelMapper.map(supportTicket, SupportTicketDtoResponse.class);
    }

    @Override
    public void deleteSupportTicket(Long id) {
        SupportTicket supportTicket =
                supportTicketRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException(SUPPORT_TICKET_NOT_EXIST));

        supportTicketRepository.delete(supportTicket);
    }
}
