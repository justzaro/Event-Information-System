package com.example.eventinformationsystembackend.controller;

import com.example.eventinformationsystembackend.dto.OrderDtoResponse;
import com.example.eventinformationsystembackend.service.OrderService;

import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping("/{id}")
    public OrderDtoResponse getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }

    @GetMapping("/all/{username}")
    public List<OrderDtoResponse> getAllOrdersForUser(@PathVariable String username) {
        return orderService.getAllOrdersForUsers(username);
    }

    @PostMapping("/{username}")
    public void createOrder(@PathVariable String username,
                            @PathParam("couponCode") String couponCode) {
        orderService.createOrder(username, couponCode);
    }

    @GetMapping("/prices/last/{ordersCount}")
    public List<Integer> getPricesOfLastThirtyOrders(@PathVariable int ordersCount) {
        return orderService.getPricesOfLastSelectedOrders(ordersCount);
    }
}
