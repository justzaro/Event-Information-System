package com.example.eventinformationsystembackend.controller;

import com.example.eventinformationsystembackend.dto.SupportTicketDto;
import com.example.eventinformationsystembackend.dto.SupportTicketDtoResponse;
import com.example.eventinformationsystembackend.dto.SupportTicketReplyDto;
import com.example.eventinformationsystembackend.dto.SupportTicketReplyDtoResponse;
import com.example.eventinformationsystembackend.service.SupportTicketService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/support-tickets")
public class SupportTicketController {

    private final SupportTicketService supportTicketService;

    public SupportTicketController(SupportTicketService supportTicketService) {
        this.supportTicketService = supportTicketService;
    }

    @GetMapping
    public List<SupportTicketDtoResponse> getAllSupportTickets() {
        return supportTicketService.getAllSupportTickets();
    }

    @GetMapping("/{username}")
    public List<SupportTicketDtoResponse> getAllSupportTicketsForUser(
            @PathVariable("username") String username) {
        return supportTicketService.getAllSupportTicketsForUser(username);
    }

    @PostMapping("/{username}")
    public SupportTicketDtoResponse createSupportTicket(
            @RequestBody @Valid SupportTicketDto supportTicketDto,
            @PathVariable("username") String username) {
        return supportTicketService.createSupportTicket(supportTicketDto,
                username);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupportTicket(@PathVariable("id") Long id) {
        supportTicketService.deleteSupportTicket(id);
        return ResponseEntity.ok().build();
    }
}
