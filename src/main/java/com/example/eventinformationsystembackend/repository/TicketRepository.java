package com.example.eventinformationsystembackend.repository;

import com.example.eventinformationsystembackend.model.OrderItem;
import com.example.eventinformationsystembackend.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findAllByOrderItem(OrderItem orderItem);
    Optional<Ticket> findByCode(String code);
}
