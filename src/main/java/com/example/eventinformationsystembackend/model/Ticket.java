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
@Table(name = "tickets")
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_id")
    private Long id;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "qr_code_image_path", nullable = false)
    private String qrCodeImagePath;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "is_checked", nullable = false)
    private Boolean isChecked;

    @ManyToOne
    @JoinColumn(name = "order_item_id", referencedColumnName = "order_item_id", nullable = false)
    private OrderItem orderItem;

    @ManyToOne
    @JoinColumn(name = "event_id", referencedColumnName = "event_id", nullable = false)
    private Event event;

    @Override
    public String toString() {
        return "Ticket{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", qrCodeImagePath='" + qrCodeImagePath + '\'' +
                ", createAt=" + createdAt +
                ", isChecked=" + isChecked +
                ", orderItemId=" + orderItem.getId() +
                '}';
    }
}
