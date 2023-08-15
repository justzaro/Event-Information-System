package com.example.eventinformationsystembackend.exception;

public class EmailAlreadyConfirmedException extends RuntimeException {
    public EmailAlreadyConfirmedException(String message) {
        super(message);
    }
}
