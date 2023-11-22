package com.example.eventinformationsystembackend.controller;

import com.example.eventinformationsystembackend.dto.CouponDto;
import com.example.eventinformationsystembackend.dto.CouponDtoResponse;
import com.example.eventinformationsystembackend.service.implementation.CouponServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/coupons")
public class CouponController {
    private final CouponServiceImpl couponServiceImpl;

    @Autowired
    public CouponController(CouponServiceImpl couponServiceImpl) {
        this.couponServiceImpl = couponServiceImpl;
    }

    @GetMapping
    public List<CouponDtoResponse> getAllCoupons() {
        return couponServiceImpl.getAllCoupons();
    }

    @PostMapping("/single-use")
    public void generateSingleUseCoupons(@RequestBody @Valid CouponDto couponDto) {
        couponServiceImpl.generateSingleUseCoupons(couponDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCoupon(@PathVariable Long id) {
        couponServiceImpl.deleteCoupon(id);
        return ResponseEntity.ok().build();
    }
}
