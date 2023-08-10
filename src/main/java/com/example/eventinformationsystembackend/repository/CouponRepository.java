package com.example.eventinformationsystembackend.repository;

import com.example.eventinformationsystembackend.model.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {
    Optional<Coupon> findCouponByCouponCode(String couponCode);

    @Transactional
    @Modifying
    @Query(value = "UPDATE coupons SET is_used = true WHERE coupon_id = :couponId",
           nativeQuery = true)
    void updateIsUsedColumnToTrue(@Param("couponId") Long couponId);
}
