package com.example.eventinformationsystembackend.service;

import com.example.eventinformationsystembackend.dto.OrderDtoResponse;

import java.util.List;

public interface OrderService {
    void createOrder(String username, String couponCode);

    Double getCouponDiscount(String username, String couponCode);

    OrderDtoResponse getOrderById(Long id);

    List<OrderDtoResponse> getAllOrdersForUsers(String username);

    List<Integer> getPricesOfLastSelectedOrders(int ordersCount);
}
