package com.example.eventinformationsystembackend.exception;

public class TokenHasBeenRevokedException extends RuntimeException {
    public TokenHasBeenRevokedException(String message) {
        super(message);
    }
}
