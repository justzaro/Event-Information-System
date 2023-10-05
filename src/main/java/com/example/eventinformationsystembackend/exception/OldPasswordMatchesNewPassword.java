package com.example.eventinformationsystembackend.exception;

public class OldPasswordMatchesNewPassword extends RuntimeException {
    public OldPasswordMatchesNewPassword(String message) {
        super(message);
    }
}
