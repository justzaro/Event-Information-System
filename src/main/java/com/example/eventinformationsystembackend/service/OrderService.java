package com.example.eventinformationsystembackend.service;

import com.example.eventinformationsystembackend.dto.OrderDto;
import com.example.eventinformationsystembackend.dto.OrderDtoResponse;
import com.example.eventinformationsystembackend.exception.NotEnoughSeatsException;
import com.example.eventinformationsystembackend.exception.ResourceNotFoundException;
import com.example.eventinformationsystembackend.model.Coupon;
import com.example.eventinformationsystembackend.model.Event;
import com.example.eventinformationsystembackend.model.Order;
import com.example.eventinformationsystembackend.model.User;
import com.example.eventinformationsystembackend.repository.CouponRepository;
import com.example.eventinformationsystembackend.repository.EventRepository;
import com.example.eventinformationsystembackend.repository.OrderRepository;
import com.example.eventinformationsystembackend.repository.UserRepository;
import org.aspectj.weaver.ast.Or;
import org.modelmapper.ModelMapper;
import org.springframework.aop.AopInvocationException;
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
    private final CouponService couponService;
    private final ModelMapper modelMapper;

    @Autowired
    public OrderService(OrderRepository orderRepository,
                        UserRepository userRepository,
                        EventRepository eventRepository,
                        CouponService couponService) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.couponService = couponService;
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

        double discount = 0;

        if (orderDto.getCouponCode() != null) {
            Coupon coupon = couponService.validateCoupon(orderDto.getCouponCode());
            discount = coupon.getDiscountPercentage();
            orderToCreate.setCoupon(coupon);
        }

        double totalPrice = calculateTotalPrice(orderDto, event, discount);

        orderToCreate.setTotalPrice(totalPrice);
        orderToCreate.setDateOfOrder(LocalDateTime.now());
        orderToCreate.setUser(user);
        orderToCreate.setEvent(event);

        Order newOrder = orderRepository.save(orderToCreate);

        return modelMapper.map(newOrder, OrderDtoResponse.class);
    }

    private boolean checkIfEventHasEnoughSeats(Event event, int orderedTickets) {
        int ticketsBoughtForCurrentConcert;

        //This exception is thrown if `getTicketsBoughtForEvent()` method returns null
        //This happens if there are no orders for the specified concert
        try {
            ticketsBoughtForCurrentConcert = orderRepository.getTicketsBoughtForEvent(event.getId());
        } catch (AopInvocationException e) {
            return false;
        }

        int eventCapacity = event.getCapacity();
        int availableTickets = eventCapacity - ticketsBoughtForCurrentConcert;

        return orderedTickets >= availableTickets;
    }

    private double calculateTotalPrice(OrderDto orderDto, Event event,
                                       double discount) {
        double priceWithoutDiscount = orderDto.getTicketsBought() * event.getTicketPrice();

        return priceWithoutDiscount - (priceWithoutDiscount * (discount * 0.01));
    }
}
