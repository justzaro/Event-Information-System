package com.example.eventinformationsystembackend.exception;

public class InvalidCreditCardCredentialsException extends RuntimeException {
    public InvalidCreditCardCredentialsException(String message) {
        super(message);
    }
}
