package com.example.eventinformationsystembackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class OrderInformation {
    int ticketQuantity;
    double totalPrice;
}
