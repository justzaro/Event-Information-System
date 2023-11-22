package com.example.eventinformationsystembackend.controller;

import com.example.eventinformationsystembackend.dto.OrderDtoResponse;
import com.example.eventinformationsystembackend.dto.OrderItemDtoResponse;
import com.example.eventinformationsystembackend.service.implementation.OrderItemServiceImpl;
import com.example.eventinformationsystembackend.service.implementation.OrderServiceImpl;
import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderServiceImpl orderServiceImpl;
    private final OrderItemServiceImpl orderItemServiceImpl;

    @Autowired
    public OrderController(OrderServiceImpl orderServiceImpl,
                           OrderItemServiceImpl orderItemServiceImpl) {
        this.orderServiceImpl = orderServiceImpl;
        this.orderItemServiceImpl = orderItemServiceImpl;
    }

    @GetMapping("/{id}")
    public OrderDtoResponse getOrderById(@PathVariable Long id) {
        return orderServiceImpl.getOrderById(id);
    }

    @GetMapping("/all/{username}")
    public List<OrderDtoResponse> getAllOrdersForUser(@PathVariable String username) {
        return orderServiceImpl.getAllOrdersForUsers(username);
    }

    @PostMapping("/{username}")
    public void createOrder(@PathVariable String username,
                            @PathParam("couponCode") String couponCode) {
        orderServiceImpl.createOrder(username, couponCode);
    }

    //test
    @GetMapping(path = "/order-items/{orderId}")
    public List<OrderItemDtoResponse> getOrderItems(@PathVariable Long id) {
        return orderItemServiceImpl.getOrderItems(id);
    }

    @GetMapping("/prices/last/{ordersCount}")
    public List<Integer> getPricesOfLastThirtyOrders(@PathVariable int ordersCount) {
        return orderServiceImpl.getPricesOfLastSelectedOrders(ordersCount);
    }
}
