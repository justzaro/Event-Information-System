package com.example.eventinformationsystembackend.service;

import com.example.eventinformationsystembackend.dto.OrderItemDtoResponse;

import java.util.List;

public interface OrderItemService {
    List<OrderItemDtoResponse> getOrderItems(Long orderId);
}
