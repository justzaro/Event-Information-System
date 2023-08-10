package com.example.eventinformationsystembackend.service;

import com.example.eventinformationsystembackend.common.enums.CouponType;
import com.example.eventinformationsystembackend.dto.CouponDto;
import com.example.eventinformationsystembackend.exception.CouponHasBeenUsedException;
import com.example.eventinformationsystembackend.exception.CouponHasExpiredException;
import com.example.eventinformationsystembackend.exception.ResourceNotFoundException;
import com.example.eventinformationsystembackend.model.Coupon;
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

    @Autowired
    public CouponService(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
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

         couponRepository.updateIsUsedColumnToTrue(coupon.getCouponId());

         return coupon;
    }

    public void generateSingleUseCoupons(CouponDto couponDto) {
        List<String> generatedCouponCodes =
                generateCouponCodes(couponDto.getCouponsToBeGenerated());

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

    public void generateMultiUseCoupons(CouponDto couponDto) {
        List<String> generatedCouponCodes =
                generateCouponCodes(couponDto.getCouponsToBeGenerated());

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
    }

    private List<String> generateCouponCodes(int couponToBeGenerated) {
        final String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmopqrstuvwxyz0123456789";
        Random random = new Random();

        List<String> generatedCouponCodes = new ArrayList<>();

        int couponCodeLength = 10;

        for (int i = 0; i < couponToBeGenerated; i++) {
            StringBuilder sb = new StringBuilder();

            for (int j = 0; j < couponCodeLength; j++) {

                int index = random.nextInt(alphabet.length());
                char randomChar = alphabet.charAt(index);
                sb.append(randomChar);
            }

            String generatedString = sb.toString();
            generatedCouponCodes.add(generatedString);
        }

        return generatedCouponCodes;
    }
}
