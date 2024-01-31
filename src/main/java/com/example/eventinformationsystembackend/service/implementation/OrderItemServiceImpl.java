package com.example.eventinformationsystembackend.service.implementation;

import com.example.eventinformationsystembackend.dto.OrderItemDtoResponse;
import com.example.eventinformationsystembackend.model.OrderItem;
import com.example.eventinformationsystembackend.repository.OrderItemRepository;
import com.example.eventinformationsystembackend.service.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {
    private final OrderItemRepository orderItemRepository;
    private final ModelMapper modelMapper = new ModelMapper();

    @Override
    public List<OrderItemDtoResponse> getOrderItems(Long orderId) {
        List<OrderItem> orderItems = orderItemRepository.findAllByOrderId(orderId);

        return orderItems
               .stream()
               .map(orderItem -> modelMapper.map(orderItem, OrderItemDtoResponse.class))
               .collect(Collectors.toList());
    }
}
