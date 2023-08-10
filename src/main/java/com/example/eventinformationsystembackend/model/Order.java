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
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @Column(name = "tickets_bought", nullable = false)
    private Integer ticketsBought;

    @Column(name = "promo_code")
    private String promoCode;

    @Column(name = "total_price", nullable = false)
    private Double totalPrice;

    @Column(name = "date_of_order", nullable = false)
    private LocalDateTime dateOfOrder;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "event_id", referencedColumnName = "event_id", nullable = false)
    private Event event;

    @OneToOne
    @JoinColumn(name = "coupon_id", referencedColumnName = "coupon_id")
    private Coupon coupon;
}
