package com.example.eventinformationsystembackend.controller;

import com.example.eventinformationsystembackend.dto.CouponDto;
import com.example.eventinformationsystembackend.service.CouponService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/coupons")
public class CouponController {
    private final CouponService couponService;

    @Autowired
    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @PostMapping(path = "/single-use")
    public void generateSingleUseCoupons(@RequestBody @Valid CouponDto couponDto) {
        couponService.generateSingleUseCoupons(couponDto);
    }
}
