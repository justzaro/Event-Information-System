package com.example.eventinformationsystembackend.controller;

import com.example.eventinformationsystembackend.dto.OrderDto;
import com.example.eventinformationsystembackend.dto.OrderDtoResponse;
import com.example.eventinformationsystembackend.dto.OrderItemDtoResponse;
import com.example.eventinformationsystembackend.model.OrderItem;
import com.example.eventinformationsystembackend.service.OrderItemService;
import com.example.eventinformationsystembackend.service.OrderService;
import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/orders")
public class OrderController {
    private final OrderService orderService;
    private final OrderItemService orderItemService;

    @Autowired
    public OrderController(OrderService orderService,
                           OrderItemService orderItemService) {
        this.orderService = orderService;
        this.orderItemService = orderItemService;
    }

    @GetMapping(path = "/{id}")
    public OrderDtoResponse getOrderById(@PathVariable("id") Long id) {
        return orderService.getOrderById(id);
    }

    @GetMapping(path = "/all/{username}")
    public List<OrderDtoResponse> getAllOrdersForUser(@PathVariable("username") String username) {
        return orderService.getAllOrdersForUsers(username);
    }

    @PostMapping(path = "/create/{username}")
    public void createOrder(@PathVariable("username") String username,
                            @PathParam("couponCode") String couponCode) {
        orderService.createOrder(username, couponCode);
    }

    //test
    @GetMapping(path = "/order-items/{orderId}")
    public List<OrderItemDtoResponse> getOrderItems(@PathVariable("orderId") Long id) {
        return orderItemService.getOrderItems(id);
    }
}
