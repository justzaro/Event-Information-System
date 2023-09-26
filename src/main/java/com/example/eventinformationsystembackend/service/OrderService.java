package com.example.eventinformationsystembackend.service;

import com.example.eventinformationsystembackend.dto.*;
import com.example.eventinformationsystembackend.exception.EmptyCartException;
import com.example.eventinformationsystembackend.exception.NotEnoughSeatsException;
import com.example.eventinformationsystembackend.exception.ResourceNotFoundException;
import com.example.eventinformationsystembackend.model.*;
import com.example.eventinformationsystembackend.repository.EventRepository;
import com.example.eventinformationsystembackend.repository.OrderItemRepository;
import com.example.eventinformationsystembackend.repository.OrderRepository;
import com.example.eventinformationsystembackend.repository.UserRepository;
import org.aspectj.weaver.ast.Or;
import org.modelmapper.ModelMapper;
import org.springframework.aop.AopInvocationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.eventinformationsystembackend.common.ExceptionMessages.*;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CouponService couponService;
    private final CartItemService cartItemService;
    private final EventService eventService;
    private final TicketService ticketService;
    private final GenerationService generationService;
    private final OrderItemService orderItemService;
    private final OrderItemRepository orderItemRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public OrderService(OrderRepository orderRepository,
                        UserRepository userRepository,
                        EventRepository eventRepository,
                        CouponService couponService,
                        CartItemService cartItemService,
                        EventService eventService,
                        TicketService ticketService,
                        GenerationService generationService,
                        OrderItemService orderItemService,
                        OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.couponService = couponService;
        this.cartItemService = cartItemService;
        this.eventService = eventService;
        this.ticketService = ticketService;
        this.generationService = generationService;
        this.orderItemRepository = orderItemRepository;
        this.orderItemService = orderItemService;
        this.modelMapper = new ModelMapper();
    }

    public OrderDtoResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ORDER_DOES_NOT_EXIST));

        return modelMapper.map(order, OrderDtoResponse.class);
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

    private Double getCartItemsTotalPriceWithoutCoupon(String username) {
        List<CartItemDtoResponse> cartItems =
                cartItemService.getAllCartItemsForUser(username);

        double totalPrice = 0;

        for (CartItemDtoResponse cartItem : cartItems) {
            totalPrice += cartItem.getTotalPrice();
        }
        System.out.println(totalPrice);
        return totalPrice;
    }

    public Double getCartItemsTotalPriceWithCoupon(String username, String couponCode) {
        if (userRepository.findUserByUsername(username).isEmpty()) {
            throw new ResourceNotFoundException(USER_DOES_NOT_EXIST);
        }
        Coupon coupon = couponService.validateCoupon(couponCode);

        double totalPrice = getCartItemsTotalPriceWithoutCoupon(username);
        double discount = coupon.getDiscountPercentage() / 100;

        return totalPrice - (totalPrice * discount);
    }

    public void createOrder(String username, String couponCode) {

        List<CartItemDtoResponse> cartItems =
                cartItemService.getAllCartItemsForUser(username);

        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(USER_DOES_NOT_EXIST));

        if (cartItems.isEmpty()) {
            throw new EmptyCartException(EMPTY_CART);
        }

        int totalTicketQuantity = 0;
        double totalPrice = 0;

        for (CartItemDtoResponse cartItem : cartItems) {
            Event cartItemEvent = eventRepository.findEventByName(cartItem.getEventName())
                    .orElseThrow(() -> new ResourceNotFoundException(EVENT_DOES_NOT_EXIST));

            if (!eventService.checkIfEventHasEnoughSeats(cartItemEvent, cartItem.getTicketQuantity())) {
                throw new NotEnoughSeatsException(NOT_ENOUGH_SEATS);
            }

            totalTicketQuantity += cartItem.getTicketQuantity();
            totalPrice += cartItem.getTotalPrice();
        }

        Coupon coupon = null;

        Order order = new Order();
        order.setOriginalPrice(totalPrice);

        if (couponCode != null) {
            coupon = couponService.validateCoupon(couponCode);
            totalPrice = calculateTotalPriceWithDiscount(totalPrice, coupon);
            couponService.setCouponAsUsed(coupon);
        }
        
        order.setUser(user);
        order.setDateOfOrder(LocalDateTime.now());
        order.setCoupon(coupon);
        order.setTicketsBought(totalTicketQuantity);
        order.setTotalPrice(totalPrice);
        order.setOrderItems(new ArrayList<>());

        Order createdOrder = orderRepository.save(order);

        ticketService.createTickets(cartItemService.getAllCartItemsForUser(user), createdOrder);

        cartItemService.removeAllCartItemsForUser(user);

        try {
            generationService.generateOrderReceivedEmailTemplate(createdOrder, user);
            generationService.generateOrderedTicketsEmailTemplate(createdOrder, user);
        } catch (IOException e) {

        }

    }
    public Double calculateTotalPriceWithDiscount(double totalPrice, Coupon coupon) {
        double discountPercentage = coupon.getDiscountPercentage() / 100;

        return totalPrice - (totalPrice * discountPercentage);
    }
}
