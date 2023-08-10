package com.example.eventinformationsystembackend.dto;

import com.example.eventinformationsystembackend.model.Coupon;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {
    @NotNull
    private Integer ticketsBought;

    @Size(max = 255)
    private String couponCode;
}
