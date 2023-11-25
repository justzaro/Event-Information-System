package com.example.eventinformationsystembackend.service.implementation;

import com.example.eventinformationsystembackend.dto.SupportTicketReplyDto;
import com.example.eventinformationsystembackend.dto.SupportTicketReplyDtoResponse;

import com.example.eventinformationsystembackend.model.SupportTicketReply;

import com.example.eventinformationsystembackend.model.User;

import com.example.eventinformationsystembackend.repository.SupportTicketReplyRepository;

import com.example.eventinformationsystembackend.service.DataValidationService;
import com.example.eventinformationsystembackend.service.EmailService;
import com.example.eventinformationsystembackend.service.GenerationService;
import com.example.eventinformationsystembackend.service.SupportTicketReplyService;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SupportTicketReplyServiceImpl implements SupportTicketReplyService {

    private final SupportTicketReplyRepository supportTicketReplyRepository;
    private final GenerationService generationService;
    private final EmailService emailService;
    private final DataValidationService dataValidationService;
    private final ModelMapper modelMapper = new ModelMapper();

    @Override
    public SupportTicketReplyDtoResponse replyToSupportTicket
            (SupportTicketReplyDto supportTicketReplyDto, String username)
    {
        User replier = dataValidationService.getUserByUsername(username);

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
