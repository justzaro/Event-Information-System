package com.example.eventinformationsystembackend.service.implementation;

import com.example.eventinformationsystembackend.model.*;

import com.example.eventinformationsystembackend.repository.OrderItemRepository;
import com.example.eventinformationsystembackend.repository.TicketRepository;

import com.example.eventinformationsystembackend.service.GenerationService;
import com.example.eventinformationsystembackend.service.TicketService;

import com.google.zxing.WriterException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.eventinformationsystembackend.common.TicketInformation.*;
import static com.example.eventinformationsystembackend.common.QRCodeDetails.*;
import static com.example.eventinformationsystembackend.common.FilePaths.*;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {
    private final TicketRepository ticketRepository;
    private final GenerationService generationService;
    private final OrderItemRepository orderItemRepository;

    @Override
    public String verifyTicket(String ticketCode) {
        Optional<Ticket> ticket = ticketRepository.findByCode(ticketCode);

        if (ticket.isPresent()) {
            if (!ticket.get().getIsChecked()) {
                ticket.get().setIsChecked(true);
                ticketRepository.save(ticket.get());
                return "valid-ticket";
            } else {
                return "checked-ticket";
            }
        }
        return "invalid-ticket";
    }

    @Override
    public void createTickets(List<CartItem> cartItems, Order order) {

        for (CartItem cartItem : cartItems) {
            List<Ticket> tickets = new ArrayList<>();

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            OrderItem createdOrderedItem = orderItemRepository.save(orderItem);

            int ticketsQuantity = cartItem.getTicketQuantity();

            List<String> ticketCodes = generationService.
                    generateCodes(ticketsQuantity, TICKET_CODE_LENGTH, TICKET_CODE_ALPHABET);

            for (int i = 0; i < ticketsQuantity; i++) {

                String code = ticketCodes.get(i);
                InetAddress IP = null;
                try {
                    IP = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                String qrCodeContent = "http://" + IP.getHostAddress() + ":8080/tickets/verification/" + code;
                String path = String.format(QR_CODES_FOLDER_PATH, code + QR_CODE_IMAGE_FORMAT_EXTENSION);

                try {
                    generationService.generateTicketQrCode(qrCodeContent, path);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (WriterException e) {
                    e.printStackTrace();
                }

                Ticket ticket = new Ticket();

                ticket.setCode(code);
                ticket.setCreateAt(LocalDateTime.now());
                ticket.setIsChecked(false);
                ticket.setEvent(cartItem.getEvent());
                ticket.setQrCodeImagePath(path);
                ticket.setOrderItem(createdOrderedItem);

                Ticket createdTicket = ticketRepository.save(ticket);
                tickets.add(createdTicket);
            }

            createdOrderedItem.setTickets(tickets);
            orderItemRepository.save(createdOrderedItem);

            order.getOrderItems().add(createdOrderedItem);
        }
    }

    @Override
    public List<Integer> getSoldTicketsCountPerDayForTheLastSelectedDays(int days) {
        return ticketRepository.findSoldTicketsCountPerDayForTheLastSelectedDays(days);
    }

    @Override
    public int getSoldTicketsForEvent(Event event) {
        return ticketRepository.countAllByEvent(event);
    }
}
