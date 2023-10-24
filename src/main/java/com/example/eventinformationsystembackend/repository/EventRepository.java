package com.example.eventinformationsystembackend.repository;

import com.example.eventinformationsystembackend.common.enums.EventType;
import com.example.eventinformationsystembackend.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    Optional<Event> findEventByName(String name);

    List<Event> findAllByEventType(EventType eventType);

    @Query(value = "UPDATE events SET is_active = false WHERE event_id = :eventId",
           nativeQuery = true)
    void updateIsActiveColumn(@Param("eventId") Long eventId);

    int countAllByIsActive(Boolean isActive);

    @Query(value = "SELECT COUNT(events.event_id) FROM events WHERE start_date <= :boundaryDate AND start_date >= now()",
           nativeQuery = true)
    int countAllBetweenNowAndBoundaryDate(@Param("boundaryDate") LocalDateTime boundaryDate);

    @Query(value = """
                    SELECT COUNT(eventName) AS eventsCount FROM 
                    ( 
                        SELECT e.name AS eventName 
                        FROM events AS e 
                        JOIN tickets AS t ON t.event_id = e.event_id 
                        JOIN order_items AS oi ON oi.order_item_id = t.order_item_id 
                        JOIN orders AS o ON o.order_id = oi.order_id 
                        WHERE start_date >= :boundaryDate AND start_date <= NOW() 
                        GROUP BY e.name 
                        HAVING COUNT(e.event_id) > 0
                    ) 
                    AS ce;
                    """,
            nativeQuery = true)
    int countAllWithAtLeastOneTicketBoughtInTheLastTargetDays(@Param("boundaryDate") LocalDateTime boundaryDate);
}
