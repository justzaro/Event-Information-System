package com.example.eventinformationsystembackend.exception;

public class PostDoesNotContainImageException extends RuntimeException {
    public PostDoesNotContainImageException(String message) {
        super(message);
    }
}
