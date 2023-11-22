package com.example.eventinformationsystembackend.service.implementation;

import com.example.eventinformationsystembackend.dto.SupportTicketReplyDto;
import com.example.eventinformationsystembackend.dto.SupportTicketReplyDtoResponse;
import com.example.eventinformationsystembackend.exception.ResourceNotFoundException;
import com.example.eventinformationsystembackend.model.SupportTicketReply;
import com.example.eventinformationsystembackend.model.User;
import com.example.eventinformationsystembackend.repository.SupportTicketReplyRepository;
import com.example.eventinformationsystembackend.repository.UserRepository;
import com.example.eventinformationsystembackend.service.SupportTicketReplyService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.example.eventinformationsystembackend.common.ExceptionMessages.USER_DOES_NOT_EXIST;

@Service
public class SupportTicketReplyServiceImpl implements SupportTicketReplyService {

    private final SupportTicketReplyRepository supportTicketReplyRepository;
    private final GenerationServiceImpl generationServiceImpl;
    private final EmailServiceImpl emailServiceImpl;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public SupportTicketReplyServiceImpl(SupportTicketReplyRepository supportTicketReplyRepository,
                                         UserRepository userRepository,
                                         GenerationServiceImpl generationServiceImpl,
                                         EmailServiceImpl emailServiceImpl) {
        this.supportTicketReplyRepository = supportTicketReplyRepository;
        this.generationServiceImpl = generationServiceImpl;
        this.emailServiceImpl = emailServiceImpl;
        this.userRepository = userRepository;
        this.modelMapper = new ModelMapper();
    }

    @Override
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

        String template = generationServiceImpl.generateSupportTicketResponseTemplate(supportTicketReply, replier);
        emailServiceImpl.sendSupportTicketResponseEmail
                (supportTicketReply.getSupportTicket().getCustomerEmail(), template);

        return modelMapper.map(supportTicketReply, SupportTicketReplyDtoResponse.class);
    }
}
