package com.example.eventinformationsystembackend.controller;

import com.example.eventinformationsystembackend.dto.SupportTicketReplyDto;
import com.example.eventinformationsystembackend.dto.SupportTicketReplyDtoResponse;
import com.example.eventinformationsystembackend.service.implementation.SupportTicketReplyServiceImpl;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/support-ticket-replies")
public class SupportTicketReplyController {
    private final SupportTicketReplyServiceImpl supportTicketReplyServiceImpl;

    public SupportTicketReplyController(SupportTicketReplyServiceImpl supportTicketReplyServiceImpl) {
        this.supportTicketReplyServiceImpl = supportTicketReplyServiceImpl;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{username}")
    public SupportTicketReplyDtoResponse replyToSupportTicket(
            @Valid @RequestBody SupportTicketReplyDto supportTicketReplyDto,
            @PathVariable String username
    ) {
        return supportTicketReplyServiceImpl.replyToSupportTicket(supportTicketReplyDto,
                username);
    }
}

