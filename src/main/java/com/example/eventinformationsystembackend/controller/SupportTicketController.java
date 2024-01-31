package com.example.eventinformationsystembackend.controller;

import com.example.eventinformationsystembackend.dto.SupportTicketDto;
import com.example.eventinformationsystembackend.dto.SupportTicketDtoResponse;
import com.example.eventinformationsystembackend.service.SupportTicketService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/support-tickets")
@RequiredArgsConstructor
public class SupportTicketController {

    private final SupportTicketService supportTicketService;

    @GetMapping
    public List<SupportTicketDtoResponse> getAllSupportTickets() {
        return supportTicketService.getAllSupportTickets();
    }

    @GetMapping("/{username}")
    public List<SupportTicketDtoResponse> getAllSupportTicketsForUser(
            @PathVariable String username) {
        return supportTicketService.getAllSupportTicketsForUser(username);
    }

//    @PostMapping("/{username}")
//    public SupportTicketDtoResponse createSupportTicket(
//            @RequestBody @Valid SupportTicketDto supportTicketDto,
//            @PathVariable String username) {
//
//        return supportTicketService.createSupportTicket(supportTicketDto,
//                username);
//    }
    @PostMapping("/{username}")
    public SupportTicketDtoResponse createSupportTicket(
            @RequestBody @Valid SupportTicketDto supportTicketDto,
            @PathVariable String username) {

        return supportTicketService.createSupportTicket(supportTicketDto,
                username);
    }

    @PostMapping
    public SupportTicketDtoResponse createSupportTicketAsGuest(@RequestBody @Valid SupportTicketDto supportTicketDto) {
        return supportTicketService.createSupportTicket(supportTicketDto, null);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupportTicket(@PathVariable Long id) {
        supportTicketService.deleteSupportTicket(id);
        return ResponseEntity.ok().build();
    }
}
