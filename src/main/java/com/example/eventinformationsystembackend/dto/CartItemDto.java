package com.example.eventinformationsystembackend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDto {

    @NotNull
    private String eventName;

    @NotNull
    private String username;

    @NotNull
    @DecimalMin(value = "1", message = "Minimal ticket quantity should be 1!")
    private Integer ticketQuantity;
}
