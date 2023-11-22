package com.example.eventinformationsystembackend.service;

import com.example.eventinformationsystembackend.model.CartItem;
import com.example.eventinformationsystembackend.model.Event;
import com.example.eventinformationsystembackend.model.Order;

import java.util.List;

public interface TicketService {
    void createTickets(List<CartItem> cartItems, Order order);

    int getSoldTicketsForEvent(Event event);

    String verifyTicket(String ticketCode);

    List<Integer> getSoldTicketsCountPerDayForTheLastSelectedDays(int days);
}
