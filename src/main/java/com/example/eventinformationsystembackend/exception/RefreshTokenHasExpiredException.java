package com.example.eventinformationsystembackend.exception;

public class RefreshTokenHasExpiredException extends RuntimeException {
    public RefreshTokenHasExpiredException(String message) {
        super(message);
    }
}
