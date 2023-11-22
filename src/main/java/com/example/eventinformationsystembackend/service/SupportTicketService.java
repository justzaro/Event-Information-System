package com.example.eventinformationsystembackend.service;

import com.example.eventinformationsystembackend.dto.SupportTicketDto;
import com.example.eventinformationsystembackend.dto.SupportTicketDtoResponse;

import java.util.List;

public interface SupportTicketService {
    void deleteSupportTicket(Long id);

    SupportTicketDtoResponse createSupportTicket(SupportTicketDto supportTicketDto,
                                                 String username);

    List<SupportTicketDtoResponse> getAllSupportTickets();

    List<SupportTicketDtoResponse> getAllSupportTicketsForUser(String username);
}
