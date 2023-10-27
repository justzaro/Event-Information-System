package com.example.eventinformationsystembackend.service;

import com.example.eventinformationsystembackend.common.enums.CouponType;
import com.example.eventinformationsystembackend.controller.CouponController;
import com.example.eventinformationsystembackend.dto.CouponDto;
import com.example.eventinformationsystembackend.dto.CouponDtoResponse;
import com.example.eventinformationsystembackend.dto.EventDtoResponse;
import com.example.eventinformationsystembackend.exception.CouponHasBeenUsedException;
import com.example.eventinformationsystembackend.exception.CouponHasExpiredException;
import com.example.eventinformationsystembackend.exception.ResourceNotFoundException;
import com.example.eventinformationsystembackend.model.Coupon;
import com.example.eventinformationsystembackend.model.User;
import com.example.eventinformationsystembackend.repository.CouponRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static com.example.eventinformationsystembackend.common.CouponInformation.*;
import static com.example.eventinformationsystembackend.common.ExceptionMessages.*;

@Service
public class CouponService {
    private final CouponRepository couponRepository;
    private final GenerationService generationService;
    private final ModelMapper modelMapper;

    @Autowired
    public CouponService(CouponRepository couponRepository,
                         GenerationService generationService) {
        this.couponRepository = couponRepository;
        this.generationService = generationService;
        this.modelMapper = new ModelMapper();
    }

    public List<CouponDtoResponse> getAllCoupons() {
        List<Coupon> coupons = couponRepository.findAll();

        return coupons
                .stream()
                .map(coupon -> modelMapper.map(coupon, CouponDtoResponse.class))
                .collect(Collectors.toList());
    }

    public Coupon validateCoupon(String couponCode) {
         Coupon coupon = couponRepository.findCouponByCouponCode(couponCode)
                 .orElseThrow(() -> new ResourceNotFoundException(COUPON_IS_INVALID));

         if (coupon.getExpiresAt().isBefore(LocalDateTime.now())) {
             throw new CouponHasExpiredException(COUPON_HAS_EXPIRED);
         }

         if (coupon.getIsUsed()) {
             throw new CouponHasBeenUsedException(COUPON_HAS_BEEN_USED);
         }

         return coupon;
    }

    public void generateSingleUseCoupons(CouponDto couponDto) {
        List<String> generatedCouponCodes =
                generationService.generateCodes(
                        couponDto.getCouponsToBeGenerated(),
                        SINGLE_USE_COUPONS_CODE_LENGTH,
                        SINGLE_USE_COUPONS_ALPHABET);

        for (String generatedCouponCode : generatedCouponCodes) {
            Coupon coupon = new Coupon();

            coupon.setCouponCode(generatedCouponCode);
            coupon.setDiscountPercentage(couponDto.getDiscountPercentage());
            coupon.setCreatedAt(LocalDateTime.now());
            coupon.setExpiresAt(LocalDateTime.now().plusMonths(SINGLE_USE_COUPONS_MONTHS_UNTIL_EXPIRATION));
            coupon.setIsUsed(false);
            coupon.setCouponType(CouponType.SINGLE_USE);

            couponRepository.save(coupon);
        }
    }

    public void setCouponAsUsed(Coupon coupon) {
        if (couponRepository.findCouponByCouponCode(coupon.getCouponCode()).isEmpty()) {
            throw new ResourceNotFoundException(COUPON_IS_INVALID);
        }

        coupon.setIsUsed(true);
        couponRepository.save(coupon);
    }

    public void deleteCoupon(Long id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(COUPON_DOES_NOT_EXIST));

        couponRepository.delete(coupon);
    }
}
