package com.example.eventinformationsystembackend.repository;

import com.example.eventinformationsystembackend.model.Event;
import com.example.eventinformationsystembackend.model.OrderItem;
import com.example.eventinformationsystembackend.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findAllByOrderItem(OrderItem orderItem);
    Optional<Ticket> findByCode(String code);
    @Query(value = """
                   SELECT IFNULL(COUNT(t.ticket_id), 0) AS TicketCount
                   FROM (
                       SELECT DATE(NOW() - INTERVAL n DAY) AS Date
                       FROM numbers
                       WHERE n BETWEEN 0 AND :days - 1
                   ) ds
                   LEFT JOIN tickets t ON CAST(t.created_at AS DATE) = ds.Date
                   WHERE ds.Date >= NOW() - INTERVAL :days DAY
                   AND ds.Date < NOW()
                   GROUP BY ds.Date
                   ORDER BY ds.Date;
                   """,
            nativeQuery = true)
    List<Integer> findSoldTicketsCountPerDayForTheLastSelectedDays(@Param("days") Integer days);

    int countAllByEvent(Event event);
}
