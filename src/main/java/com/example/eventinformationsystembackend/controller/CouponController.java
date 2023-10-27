package com.example.eventinformationsystembackend.controller;

import com.example.eventinformationsystembackend.dto.CouponDto;
import com.example.eventinformationsystembackend.dto.CouponDtoResponse;
import com.example.eventinformationsystembackend.service.CouponService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/coupons")
public class CouponController {
    private final CouponService couponService;

    @Autowired
    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @GetMapping
    public List<CouponDtoResponse> getAllCoupons() {
        return couponService.getAllCoupons();
    }

    @PostMapping(path = "/single-use")
    public void generateSingleUseCoupons(@RequestBody @Valid CouponDto couponDto) {
        couponService.generateSingleUseCoupons(couponDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCoupon(@PathVariable("id") Long id) {
        couponService.deleteCoupon(id);
        return ResponseEntity.ok().build();
    }
}
