package com.example.eventinformationsystembackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "support_ticket_replies")
public class SupportTicketReply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "support_ticket_reply_id")
    private Long id;

    @Column(name = "text", columnDefinition = "TEXT")
    private String text;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "support_ticket_id", referencedColumnName = "support_ticket_id", nullable = false)
    private SupportTicket supportTicket;

    @ManyToOne
    @JoinColumn(name = "replier_id", referencedColumnName = "user_id")
    private User replier;

    @Override
    public String toString() {
        return "SupportTicketReply{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", createdAt=" + createdAt +
                ", supportTicket=" + supportTicket.getId() +
                ", replier=" + replier +
                '}';
    }
}
