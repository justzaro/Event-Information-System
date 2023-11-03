package com.example.eventinformationsystembackend.service;

import com.example.eventinformationsystembackend.dto.SupportTicketReplyDto;
import com.example.eventinformationsystembackend.dto.SupportTicketReplyDtoResponse;
import com.example.eventinformationsystembackend.exception.ResourceNotFoundException;
import com.example.eventinformationsystembackend.model.SupportTicketReply;
import com.example.eventinformationsystembackend.model.User;
import com.example.eventinformationsystembackend.repository.SupportTicketReplyRepository;
import com.example.eventinformationsystembackend.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.eventinformationsystembackend.common.ExceptionMessages.USER_DOES_NOT_EXIST;

@Service
public class SupportTicketReplyService {
    private SupportTicketReplyRepository supportTicketReplyRepository;
    private GenerationService generationService;
    private EmailService emailService;
    private UserRepository userRepository;
    private ModelMapper modelMapper;

    public SupportTicketReplyService(SupportTicketReplyRepository supportTicketReplyRepository,
                                     UserRepository userRepository,
                                     GenerationService generationService,
                                     EmailService emailService) {
        this.supportTicketReplyRepository = supportTicketReplyRepository;
        this.generationService = generationService;
        this.emailService = emailService;
        this.userRepository = userRepository;
        this.modelMapper = new ModelMapper();
    }

    public SupportTicketReplyDtoResponse replyToSupportTicket
            (SupportTicketReplyDto supportTicketReplyDto, String username)
    {
        User replier = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(USER_DOES_NOT_EXIST));

        SupportTicketReply supportTicketReply
                = modelMapper.map(supportTicketReplyDto, SupportTicketReply.class);

        supportTicketReply.setCreatedAt(LocalDateTime.now());
        supportTicketReply.setReplier(replier);

        supportTicketReply = supportTicketReplyRepository.save(supportTicketReply);

        String template = generationService.generateSupportTicketResponseTemplate(supportTicketReply, replier);
        emailService.sendSupportTicketResponseEmail
                (supportTicketReply.getSupportTicket().getCustomerEmail(), template);

        return modelMapper.map(supportTicketReply, SupportTicketReplyDtoResponse.class);
    }
}
