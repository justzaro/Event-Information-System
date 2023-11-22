package com.example.eventinformationsystembackend.service.implementation;

import com.example.eventinformationsystembackend.dto.*;
import com.example.eventinformationsystembackend.exception.EmptyCartException;
import com.example.eventinformationsystembackend.exception.InvalidCreditCardCredentialsException;
import com.example.eventinformationsystembackend.exception.NotEnoughSeatsException;
import com.example.eventinformationsystembackend.exception.ResourceNotFoundException;
import com.example.eventinformationsystembackend.model.*;
import com.example.eventinformationsystembackend.repository.EventRepository;
import com.example.eventinformationsystembackend.repository.OrderRepository;
import com.example.eventinformationsystembackend.repository.UserRepository;
import com.example.eventinformationsystembackend.service.OrderService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.eventinformationsystembackend.common.ExceptionMessages.*;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CouponServiceImpl couponServiceImpl;
    private final CartItemServiceImpl cartItemServiceImpl;
    private final EventServiceImpl eventServiceImpl;
    private final TicketServiceImpl ticketServiceImpl;
    private final GenerationServiceImpl generationServiceImpl;
    private final ModelMapper modelMapper;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository,
                            UserRepository userRepository,
                            EventRepository eventRepository,
                            CouponServiceImpl couponServiceImpl,
                            CartItemServiceImpl cartItemServiceImpl,
                            EventServiceImpl eventServiceImpl,
                            TicketServiceImpl ticketServiceImpl,
                            GenerationServiceImpl generationServiceImpl) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.couponServiceImpl = couponServiceImpl;
        this.cartItemServiceImpl = cartItemServiceImpl;
        this.eventServiceImpl = eventServiceImpl;
        this.ticketServiceImpl = ticketServiceImpl;
        this.generationServiceImpl = generationServiceImpl;
        this.modelMapper = new ModelMapper();
    }

    @Override
    public OrderDtoResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ORDER_DOES_NOT_EXIST));

        return modelMapper.map(order, OrderDtoResponse.class);
    }

    @Override
    public List<OrderDtoResponse> getAllOrdersForUsers(String username) {
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(USER_DOES_NOT_EXIST));

        List<Order> orders = orderRepository.findAllByUser(user);

        return orders
               .stream()
               .map(order -> modelMapper.map(order, OrderDtoResponse.class))
               .collect(Collectors.toList());
    }

    @Override
    public Double getCouponDiscount(String username, String couponCode) {
        if (userRepository.findUserByUsername(username).isEmpty()) {
            throw new ResourceNotFoundException(USER_DOES_NOT_EXIST);
        }
        Coupon coupon = couponServiceImpl.validateCoupon(couponCode);

        return coupon.getDiscountPercentage();
    }

    @Override
    public void createOrder(String username, String couponCode) {
        List<CartItemDtoResponse> cartItems =
                cartItemServiceImpl.getAllCartItemsForUser(username);

        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(USER_DOES_NOT_EXIST));

        if (cartItems.isEmpty()) {
            throw new EmptyCartException(EMPTY_CART);
        }

        if (user.getCreditCardNumber() == null || user.getCreditCardCvv() == null ||
            user.getCreditCardNumber().isBlank() || user.getCreditCardCvv().isBlank()) {
            throw new InvalidCreditCardCredentialsException("Credit card credentials are empty or invalid!");
        }

        int totalTicketQuantity = 0;
        double totalPrice = 0;

        for (CartItemDtoResponse cartItem : cartItems) {
            Event cartItemEvent = eventRepository.findEventByName(cartItem.getEventName())
                    .orElseThrow(() -> new ResourceNotFoundException(EVENT_DOES_NOT_EXIST));

            if (!eventServiceImpl.checkIfEventHasEnoughSeats(cartItemEvent, cartItem.getTicketQuantity())) {
                throw new NotEnoughSeatsException(NOT_ENOUGH_SEATS);
            }

            totalTicketQuantity += cartItem.getTicketQuantity();
            totalPrice += cartItem.getTotalPrice();
        }

        Coupon coupon = null;

        Order order = new Order();
        order.setOriginalPrice(totalPrice);

        if (couponCode != null) {
            coupon = couponServiceImpl.validateCoupon(couponCode);
            totalPrice = calculateTotalPriceWithDiscount(totalPrice, coupon);
            couponServiceImpl.setCouponAsUsed(coupon);
        }
        
        order.setUser(user);
        order.setDateOfOrder(LocalDateTime.now());
        order.setCoupon(coupon);
        order.setTicketsBought(totalTicketQuantity);
        order.setTotalPrice(totalPrice);
        order.setOrderItems(new ArrayList<>());

        Order createdOrder = orderRepository.save(order);

        ticketServiceImpl.createTickets(cartItemServiceImpl.getAllCartItemsForUser(user), createdOrder);

        cartItemServiceImpl.removeAllCartItemsForUser(user);

        try {
            generationServiceImpl.generateOrderReceivedEmailTemplate(createdOrder, user);
            generationServiceImpl.generateOrderedTicketsEmailTemplate(createdOrder, user);
        } catch (IOException e) {

        }

    }

    private Double calculateTotalPriceWithDiscount(double totalPrice, Coupon coupon) {
        double discountPercentage = coupon.getDiscountPercentage() / 100;

        return totalPrice - (totalPrice * discountPercentage);
    }

    @Override
    public List<Integer> getPricesOfLastSelectedOrders(int ordersCount) {
        return orderRepository.findPricesOfLastSelectedOrders(ordersCount);
    }
}
