package com.example.eventinformationsystembackend.exception;

public class NotEnoughSeatsException extends RuntimeException {
    public NotEnoughSeatsException(String message) {
        super(message);
    }
}
