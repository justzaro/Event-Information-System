package com.example.eventinformationsystembackend.service;

import com.example.eventinformationsystembackend.dto.OrderItemDtoResponse;
import com.example.eventinformationsystembackend.model.OrderItem;
import com.example.eventinformationsystembackend.repository.OrderItemRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderItemService {
    private final OrderItemRepository orderItemRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public OrderItemService(OrderItemRepository orderItemRepository) {
        this.orderItemRepository = orderItemRepository;
        modelMapper = new ModelMapper();
    }

    public List<OrderItemDtoResponse> getOrderItems(Long orderId) {

        List<OrderItem> orderItems = orderItemRepository.findAllByOrderId(orderId);

        return orderItems
               .stream()
               .map(orderItem -> modelMapper.map(orderItem, OrderItemDtoResponse.class))
               .collect(Collectors.toList());
    }
}
