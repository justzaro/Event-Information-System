package com.example.eventinformationsystembackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "support_tickets")
public class SupportTicket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "support_ticket_id")
    private Long id;

    @Column(name = "subject", nullable = false)
    private String subject;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "user_first_name", nullable = false)
    private String customerFirstName;

    @Column(name = "user_last_name", nullable = false)
    private String customerLastName;

    @Column(name = "user_email", nullable = false)
    private String customerEmail;

    @Column(name = "user_phone_number", nullable = false)
    private String customerPhoneNumber;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    @OneToMany(mappedBy = "supportTicket", cascade = CascadeType.REMOVE)
    public List<SupportTicketReply> supportTicketReplies;
}
