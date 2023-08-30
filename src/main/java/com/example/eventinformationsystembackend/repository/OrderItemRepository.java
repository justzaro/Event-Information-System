package com.example.eventinformationsystembackend.repository;

import com.example.eventinformationsystembackend.model.Order;
import com.example.eventinformationsystembackend.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findAllByOrderId(Long id);
}
