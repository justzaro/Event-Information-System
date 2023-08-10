package com.example.eventinformationsystembackend.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CouponDto {

    @NotNull
    @Min(value = 5, message = "At least 5 coupons have to be generated")
    @Max(value = 50, message = "No more than 50 coupons can be generated")
    private Integer couponsToBeGenerated;

    @NotNull
    @Min(value = 1, message = "Minimal discount percentage should be 1")
    @Max(value = 50, message = "Maximum discount percentage should be 50")
    private Double discountPercentage;
}
