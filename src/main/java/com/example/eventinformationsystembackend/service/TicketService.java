package com.example.eventinformationsystembackend.service;

import com.example.eventinformationsystembackend.dto.CartItemDtoResponse;
import com.example.eventinformationsystembackend.dto.TicketDtoResponse;
import com.example.eventinformationsystembackend.exception.ResourceNotFoundException;
import com.example.eventinformationsystembackend.model.*;
import com.example.eventinformationsystembackend.repository.OrderItemRepository;
import com.example.eventinformationsystembackend.repository.TicketRepository;
import com.google.zxing.WriterException;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import org.aspectj.apache.bcel.classfile.Unknown;
import org.aspectj.weaver.ast.Or;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.eventinformationsystembackend.common.TicketInformation.*;
import static com.example.eventinformationsystembackend.common.QRCodeDetails.*;
import static com.example.eventinformationsystembackend.common.FilePaths.*;
import static com.example.eventinformationsystembackend.common.ExceptionMessages.*;

@Service
public class TicketService {
    private final TicketRepository ticketRepository;
    private final CartItemService cartItemService;
    private final GenerationService generationService;
    private final OrderItemService orderItemService;
    private final OrderItemRepository orderItemRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public TicketService(TicketRepository ticketRepository,
                         CartItemService cartItemService,
                         GenerationService generationService,
                         OrderItemRepository orderItemRepository,
                         OrderItemService orderItemService) {
        this.ticketRepository = ticketRepository;
        this.cartItemService = cartItemService;
        this.generationService = generationService;
        this.orderItemService = orderItemService;
        this.orderItemRepository = orderItemRepository;
        this.modelMapper = new ModelMapper();
    }

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

    public List<TicketDtoResponse> getAllTicketsForOrderItem(OrderItem orderItem) {
        List<Ticket> tickets = ticketRepository.findAllByOrderItem(orderItem);

        return tickets
               .stream()
               .map(ticket -> modelMapper.map(ticket, TicketDtoResponse.class))
               .collect(Collectors.toList());
    }

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

                }
                String qrCodeContent = "http://" + IP.getHostAddress() + ":8080/tickets/verification/" + code;
                String path = String.format(QR_CODES_FOLDER_PATH, code + QR_CODE_IMAGE_FORMAT_EXTENSION);

                try {
                    generationService.generateTicketQrCode(qrCodeContent, path);
                } catch (IOException e) {

                } catch (WriterException e) {

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

    public List<Integer> getSoldTicketsCountPerDayForTheLastSelectedDays(int days) {
        return ticketRepository.findSoldTicketsCountPerDayForTheLastSelectedDays(days);
    }

    public int getSoldTicketsForEvent(Event event) {
        return ticketRepository.countAllByEvent(event);
    }
}
