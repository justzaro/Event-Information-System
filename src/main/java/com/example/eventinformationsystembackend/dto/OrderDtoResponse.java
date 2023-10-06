package com.example.eventinformationsystembackend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDtoResponse {

    @NotNull
    private Long id;

    @NotBlank
    private Integer ticketsBought;

    @NotBlank
    private String couponCode;

    @NotBlank
    private Double totalPrice;

    @NotBlank
    private Double originalPrice;

    @JsonFormat(pattern = "HH:mm:ss dd-MM-yyyy")
    private LocalDateTime dateOfOrder;

    private List<OrderItemDtoResponse> orderItems;
}
