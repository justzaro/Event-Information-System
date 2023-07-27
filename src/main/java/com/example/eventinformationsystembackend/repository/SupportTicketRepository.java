package com.example.eventinformationsystembackend.repository;

import com.example.eventinformationsystembackend.model.SupportTicket;
import com.example.eventinformationsystembackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupportTicketRepository extends JpaRepository<SupportTicket, Long> {
    List<SupportTicket> findAllByUser(User user);
}
