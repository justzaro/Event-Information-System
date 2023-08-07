package com.example.eventinformationsystembackend.service;

import com.example.eventinformationsystembackend.dto.OrderDto;
import com.example.eventinformationsystembackend.dto.OrderDtoResponse;
import com.example.eventinformationsystembackend.exception.NotEnoughSeatsException;
import com.example.eventinformationsystembackend.exception.ResourceNotFoundException;
import com.example.eventinformationsystembackend.model.Event;
import com.example.eventinformationsystembackend.model.Order;
import com.example.eventinformationsystembackend.model.User;
import com.example.eventinformationsystembackend.repository.EventRepository;
import com.example.eventinformationsystembackend.repository.OrderRepository;
import com.example.eventinformationsystembackend.repository.UserRepository;
import org.aspectj.weaver.ast.Or;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.eventinformationsystembackend.common.ExceptionMessages.*;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public OrderService(OrderRepository orderRepository,
                        UserRepository userRepository,
                        EventRepository eventRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.modelMapper = new ModelMapper();
    }

    public List<OrderDtoResponse> getAllOrdersForUsers(String username) {
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(USER_DOES_NOT_EXIST));

        List<Order> orders = orderRepository.findAllByUser(user);

        return orders
               .stream()
               .map(order -> modelMapper.map(order, OrderDtoResponse.class))
               .collect(Collectors.toList());
    }

    public OrderDtoResponse createOrder(String username, Long eventId,
                            OrderDto orderDto) {
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(USER_DOES_NOT_EXIST));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException(EVENT_DOES_NOT_EXIST));

        if (checkIfEventHasEnoughSeats(event, orderDto.getTicketsBought())) {
            throw new NotEnoughSeatsException(NOT_ENOUGH_SEATS);
        }

        Order orderToCreate = modelMapper.map(orderDto, Order.class);

        orderToCreate.setTotalPrice(orderDto.getTicketsBought() * event.getTicketPrice());
        orderToCreate.setDateOfOrder(LocalDateTime.now());
        orderToCreate.setUser(user);
        orderToCreate.setEvent(event);

        Order newOrder = orderRepository.save(orderToCreate);

        return modelMapper.map(newOrder, OrderDtoResponse.class);
    }

    public boolean checkIfEventHasEnoughSeats(Event event, int orderedTickets) {
        int ticketsBoughtForCurrentConcert = orderRepository.getTicketsBoughtForEvent(event.getId());
        int eventCapacity = event.getCapacity();
        int availableTickets = eventCapacity - ticketsBoughtForCurrentConcert;

        return orderedTickets >= availableTickets;
    }
}
