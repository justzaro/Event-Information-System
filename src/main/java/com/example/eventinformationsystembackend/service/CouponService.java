package com.example.eventinformationsystembackend.service;

import com.example.eventinformationsystembackend.dto.CouponDto;
import com.example.eventinformationsystembackend.dto.CouponDtoResponse;
import com.example.eventinformationsystembackend.model.Coupon;

import java.util.List;

public interface CouponService {
    void generateSingleUseCoupons(CouponDto couponDto);

    void setCouponAsUsed(Coupon coupon);

    void deleteCoupon(Long id);

    Coupon validateCoupon(String couponCode);

    List<CouponDtoResponse> getAllCoupons();
}
