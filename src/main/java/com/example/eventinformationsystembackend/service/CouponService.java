package com.example.eventinformationsystembackend.service;

import com.example.eventinformationsystembackend.common.enums.CouponType;
import com.example.eventinformationsystembackend.dto.CouponDto;
import com.example.eventinformationsystembackend.exception.CouponHasBeenUsedException;
import com.example.eventinformationsystembackend.exception.CouponHasExpiredException;
import com.example.eventinformationsystembackend.exception.ResourceNotFoundException;
import com.example.eventinformationsystembackend.model.Coupon;
import com.example.eventinformationsystembackend.model.User;
import com.example.eventinformationsystembackend.repository.CouponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.example.eventinformationsystembackend.common.CouponInformation.*;
import static com.example.eventinformationsystembackend.common.ExceptionMessages.*;

@Service
public class CouponService {
    private final CouponRepository couponRepository;
    private final GenerationService generationService;

    @Autowired
    public CouponService(CouponRepository couponRepository,
                         GenerationService generationService) {
        this.couponRepository = couponRepository;
        this.generationService = generationService;
    }

    //todo maybe pass total price here and return the result
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

/*    public void generateMultiUseCoupons(CouponDto couponDto) {
        generationService.generateCodes(
                couponDto.getCouponsToBeGenerated(),
                SINGLE_USE_COUPONS_CODE_LENGTH,
                SINGLE_USE_COUPONS_ALPHABET);

        for (String generatedCouponCode : generatedCouponCodes) {
            Coupon coupon = new Coupon();

            coupon.setCouponCode(generatedCouponCode);
            coupon.setDiscountPercentage(couponDto.getDiscountPercentage());
            coupon.setCreatedAt(LocalDateTime.now());
            coupon.setExpiresAt(LocalDateTime.now().plusMonths(MULTI_USE_COUPONS_DAYS_UNTIL_EXPIRATION));
            coupon.setIsUsed(false);
            coupon.setCouponType(CouponType.MULTI_USE);

            couponRepository.save(coupon);
        }
    }*/
}
