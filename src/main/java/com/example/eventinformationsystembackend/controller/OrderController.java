package com.example.eventinformationsystembackend.controller;

import com.example.eventinformationsystembackend.dto.OrderDto;
import com.example.eventinformationsystembackend.dto.OrderDtoResponse;
import com.example.eventinformationsystembackend.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/orders")
public class OrderController {
    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping(path = "/{username}")
    public List<OrderDtoResponse> getAllOrdersForUser(@PathVariable("username") String username) {
        return orderService.getAllOrdersForUsers(username);
    }

    @PostMapping(path = "/create/{username}/{eventId}")
    public OrderDtoResponse createOrder(@PathVariable("username") String username,
                                        @PathVariable("eventId") Long eventId,
                                        @RequestBody @Valid OrderDto orderDto) {
        return orderService.createOrder(username, eventId, orderDto);
    }
}
