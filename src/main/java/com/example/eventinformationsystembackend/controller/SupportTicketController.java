package com.example.eventinformationsystembackend.controller;

import com.example.eventinformationsystembackend.dto.SupportTicketDto;
import com.example.eventinformationsystembackend.dto.SupportTicketDtoResponse;
import com.example.eventinformationsystembackend.service.SupportTicketService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/support-tickets")
public class SupportTicketController {

    private final SupportTicketService supportTicketService;

    public SupportTicketController(SupportTicketService supportTicketService) {
        this.supportTicketService = supportTicketService;
    }

    @GetMapping(path = "/{username}")
    public List<SupportTicketDtoResponse> getAllSupportTicketsForUser(
            @PathVariable("username") String username) {
        return supportTicketService.getAllSupportTicketsForUser(username);
    }

    @PostMapping(path = "/create/{username}")
    public SupportTicketDtoResponse createSupportTicket(
            @RequestBody @Valid SupportTicketDto supportTicketDto,
            @PathVariable("username") String username) {
        return supportTicketService.createSupportTicket(supportTicketDto,
                username);
    }
}
