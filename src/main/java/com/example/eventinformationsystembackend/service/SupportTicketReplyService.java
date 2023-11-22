package com.example.eventinformationsystembackend.service;

import com.example.eventinformationsystembackend.dto.SupportTicketReplyDto;
import com.example.eventinformationsystembackend.dto.SupportTicketReplyDtoResponse;

public interface SupportTicketReplyService {
    SupportTicketReplyDtoResponse replyToSupportTicket
            (SupportTicketReplyDto supportTicketReplyDto, String username);
}
