package com.example.eventinformationsystembackend.dto;

import com.example.eventinformationsystembackend.common.enums.CouponType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CouponDtoResponse {
    private Long couponId;

    private String couponCode;

    private Double discountPercentage;

    @JsonFormat(pattern = "HH:mm dd-MM-yyyy")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "HH:mm dd-MM-yyyy")
    private LocalDateTime expiresAt;

    private Boolean isUsed;

    private CouponType couponType;

    private OrderDtoResponse order;
}
