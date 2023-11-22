package com.example.eventinformationsystembackend.controller;

import com.example.eventinformationsystembackend.dto.SupportTicketDto;
import com.example.eventinformationsystembackend.dto.SupportTicketDtoResponse;
import com.example.eventinformationsystembackend.service.implementation.SupportTicketServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/support-tickets")
public class SupportTicketController {

    private final SupportTicketServiceImpl supportTicketServiceImpl;

    public SupportTicketController(SupportTicketServiceImpl supportTicketServiceImpl) {
        this.supportTicketServiceImpl = supportTicketServiceImpl;
    }

    @GetMapping
    public List<SupportTicketDtoResponse> getAllSupportTickets() {
        return supportTicketServiceImpl.getAllSupportTickets();
    }

    @GetMapping("/{username}")
    public List<SupportTicketDtoResponse> getAllSupportTicketsForUser(
            @PathVariable String username) {
        return supportTicketServiceImpl.getAllSupportTicketsForUser(username);
    }

    @PostMapping("/{username}")
    public SupportTicketDtoResponse createSupportTicket(
            @RequestBody @Valid SupportTicketDto supportTicketDto,
            @PathVariable String username) {
        return supportTicketServiceImpl.createSupportTicket(supportTicketDto,
                username);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupportTicket(@PathVariable Long id) {
        supportTicketServiceImpl.deleteSupportTicket(id);
        return ResponseEntity.ok().build();
    }
}
