package com.example.eventinformationsystembackend.service;

import com.example.eventinformationsystembackend.dto.OrderItemDtoResponse;
import com.example.eventinformationsystembackend.dto.TicketDtoResponse;
import com.example.eventinformationsystembackend.model.User;

import java.util.List;

public interface EmailService {
    void sendConfirmationEmail(User user, String link);

    void sendOrderEmail(User user, String text,
                        List<OrderItemDtoResponse> orderItems);

    void sendTicketsEmail(User user, String text, List<String> ticketsPdfPaths,
                          List<TicketDtoResponse> tickets);

    void sendSupportTicketReceivedEmail(String receiverEmail, String text);

    void sendSupportTicketResponseEmail(String receiverEmail, String text);
}
