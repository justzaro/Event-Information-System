package com.example.eventinformationsystembackend.dto;

import com.example.eventinformationsystembackend.model.Order;
import com.example.eventinformationsystembackend.model.Ticket;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDtoResponse {
    private Long id;
    private List<TicketDtoResponse> tickets;

    //private OrderDtoResponse order;

}
