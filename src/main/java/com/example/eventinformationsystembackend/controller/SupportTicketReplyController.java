package com.example.eventinformationsystembackend.controller;

import com.example.eventinformationsystembackend.dto.SupportTicketReplyDto;
import com.example.eventinformationsystembackend.dto.SupportTicketReplyDtoResponse;
import com.example.eventinformationsystembackend.service.SupportTicketReplyService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/support-ticket-replies")
@RequiredArgsConstructor
public class SupportTicketReplyController {
    private final SupportTicketReplyService supportTicketReplyService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{username}")
    public SupportTicketReplyDtoResponse replyToSupportTicket(
            @Valid @RequestBody SupportTicketReplyDto supportTicketReplyDto,
            @PathVariable String username
    ) {
        return supportTicketReplyService.replyToSupportTicket(supportTicketReplyDto,
                username);
    }
}

