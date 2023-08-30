package com.example.eventinformationsystembackend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDtoResponse {
    @NotNull
    private String eventPicturePath;

    @NotNull
    private String eventName;

    @NotNull
    private String eventLocation;

    @NotNull
    private Integer ticketQuantity;

    @NotNull
    private Double ticketPrice;

    @NotNull
    private Double totalPrice;
}
