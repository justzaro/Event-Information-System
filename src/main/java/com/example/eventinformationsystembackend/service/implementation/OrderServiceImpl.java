package com.example.eventinformationsystembackend.service.implementation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import com.example.eventinformationsystembackend.dto.OrderInformation;
import com.example.eventinformationsystembackend.model.*;
import com.example.eventinformationsystembackend.service.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.example.eventinformationsystembackend.dto.CartItemDtoResponse;
import com.example.eventinformationsystembackend.dto.OrderDtoResponse;

import com.example.eventinformationsystembackend.exception.EmptyCartException;
import com.example.eventinformationsystembackend.exception.InvalidCreditCardCredentialsException;
import com.example.eventinformationsystembackend.exception.NotEnoughSeatsException;
import com.example.eventinformationsystembackend.exception.ResourceNotFoundException;

import com.example.eventinformationsystembackend.repository.EventRepository;
import com.example.eventinformationsystembackend.repository.OrderRepository;
import com.example.eventinformationsystembackend.repository.UserRepository;

import static com.example.eventinformationsystembackend.common.ExceptionMessages.*;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CouponService couponService;
    private final CartItemService cartItemService;
    private final EventService eventService;
    private final TicketService ticketService;
    private final GenerationService generationService;
    private final DataValidationService dataValidationService;
    private final ModelMapper modelMapper = new ModelMapper();

    @Override
    public OrderDtoResponse getOrderById(Long id) {
        Order order = getOrder(id);
        return modelMapper.map(order, OrderDtoResponse.class);
    }

    @Override
    public List<OrderDtoResponse> getAllOrdersForUsers(String username) {
        User user = dataValidationService.getUserByUsername(username);

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
        Coupon coupon = couponService.validateCoupon(couponCode);

        return coupon.getDiscountPercentage();
    }

    @Override
    public void createOrder(String username, String couponCode) {
        List<CartItemDtoResponse> cartItems =
                cartItemService.getAllCartItemsForUser(username);

        User user = dataValidationService.getUserByUsername(username);

        validateCartAndCreditCardDetails(cartItems, user);
        OrderInformation orderInformation = getOrderInformation(cartItems);

        Order order = new Order();
        setOrderDetails(user, order, orderInformation, couponCode);

        order = orderRepository.save(order);

        ticketService.createTickets(cartItemService.getAllCartItemsForUser(user), order);
        cartItemService.removeAllCartItemsForUser(user);
        generationService.generateOrderReceivedEmailTemplate(order, user);
        generationService.generateOrderedTicketsEmailTemplate(order, user);
    }

    private Double calculateTotalPriceWithDiscount(double totalPrice, Coupon coupon) {
        double discountPercentage = coupon.getDiscountPercentage() / 100;
        return totalPrice - (totalPrice * discountPercentage);
    }

    @Override
    public List<Integer> getPricesOfLastSelectedOrders(int ordersCount) {
        return orderRepository.findPricesOfLastSelectedOrders(ordersCount);
    }

    private Order getOrder(Long id) {
        return dataValidationService.
                getResourceByIdOrThrowException(id, Order.class, ORDER_DOES_NOT_EXIST);
    }

    private void validateCartAndCreditCardDetails(List<CartItemDtoResponse> cartItems, User user) {
        if (cartItems.isEmpty()) {
            throw new EmptyCartException(EMPTY_CART);
        }

        if (user.getCreditCardNumber() == null || user.getCreditCardCvv() == null ||
            user.getCreditCardNumber().isBlank() || user.getCreditCardCvv().isBlank()) {
            throw new InvalidCreditCardCredentialsException("Credit card credentials are empty or invalid!");
        }
    }

    private OrderInformation getOrderInformation(List<CartItemDtoResponse> cartItems) {
        int totalTicketQuantity = 0;
        double totalPrice = 0;

        for (CartItemDtoResponse cartItem : cartItems) {
            System.out.println(cartItem.getId());
            System.out.println(cartItem.getTotalPrice());
            Event cartItemEvent = eventRepository.findEventByName(cartItem.getEventName())
                    .orElseThrow(() -> new ResourceNotFoundException(EVENT_DOES_NOT_EXIST));

            if (!eventService.checkIfEventHasEnoughSeats(cartItemEvent, cartItem.getTicketQuantity())) {
                throw new NotEnoughSeatsException(NOT_ENOUGH_SEATS);
            }

            totalTicketQuantity += cartItem.getTicketQuantity();
            totalPrice += cartItem.getTotalPrice();
            System.out.println(totalPrice);
        }

        return new OrderInformation(totalTicketQuantity, totalPrice);
    }

    private void setOrderDetails(User user, Order order, OrderInformation orderInformation,
                                 String couponCode) {
        double totalPrice = orderInformation.getTotalPrice();
        Coupon coupon = null;
        order.setOriginalPrice(totalPrice);

        if (couponCode != null) {
            coupon = couponService.validateCoupon(couponCode);
            totalPrice = calculateTotalPriceWithDiscount(totalPrice, coupon);
            couponService.setCouponAsUsed(coupon);
        }

        order.setUser(user);
        order.setDateOfOrder(LocalDateTime.now());
        order.setCoupon(coupon);
        order.setTicketsBought(orderInformation.getTicketQuantity());
        order.setTotalPrice(totalPrice);
        order.setOrderItems(new ArrayList<>());
    }
}