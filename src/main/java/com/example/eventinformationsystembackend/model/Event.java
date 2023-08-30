package com.example.eventinformationsystembackend.model;

import com.example.eventinformationsystembackend.common.enums.Currency;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.aspectj.weaver.ast.Or;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "location", nullable = false)
    private String location;

    @Column(name = "event_picture_name", nullable = false)
    private String eventPictureName;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency")
    private Currency currency;

    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @Column(name = "ticket_price", nullable = false)
    private Double ticketPrice;

    @Column(name = "event_picture_path", nullable = false)
    private String eventPicturePath;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

/*    @OneToMany(mappedBy = "event", cascade = CascadeType.REMOVE)
    private List<Order> orders;*/

    @ManyToMany()
    @JoinTable(
            name = "events_artists",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "artist_id")
    )
    private Set<Artist> artists;

    @OneToMany(mappedBy = "event")
    private List<CartItem> cartItems;

    @OneToMany(mappedBy = "event")
    private List<Ticket> tickets;
}
