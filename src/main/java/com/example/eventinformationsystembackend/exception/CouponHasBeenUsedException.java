package com.example.eventinformationsystembackend.exception;

public class CouponHasBeenUsedException extends RuntimeException {
    public CouponHasBeenUsedException(String message) {
        super(message);
    }
}
