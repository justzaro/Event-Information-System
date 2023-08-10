package com.example.eventinformationsystembackend.exception;

public class CouponHasExpiredException extends RuntimeException {
    public CouponHasExpiredException(String message) {
        super(message);
    }
}
