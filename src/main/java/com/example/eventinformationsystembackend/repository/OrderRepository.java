package com.example.eventinformationsystembackend.repository;

import com.example.eventinformationsystembackend.model.Event;
import com.example.eventinformationsystembackend.model.Order;
import com.example.eventinformationsystembackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query(value = "SELECT SUM(tickets_bought) FROM orders WHERE event_id = :eventId",
           nativeQuery = true)
    int getTicketsBoughtForEvent(@Param("eventId") Long eventId);

    List<Order> findAllByUser(User user);
}
