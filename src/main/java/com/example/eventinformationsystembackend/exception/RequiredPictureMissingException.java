package com.example.eventinformationsystembackend.exception;

public class RequiredPictureMissingException extends RuntimeException {
    public RequiredPictureMissingException(String message) {
        super(message);
    }
}
