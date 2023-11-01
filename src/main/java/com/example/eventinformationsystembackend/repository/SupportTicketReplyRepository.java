package com.example.eventinformationsystembackend.repository;

import com.example.eventinformationsystembackend.model.SupportTicketReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupportTicketReplyRepository extends JpaRepository<SupportTicketReply, Long> {
}
