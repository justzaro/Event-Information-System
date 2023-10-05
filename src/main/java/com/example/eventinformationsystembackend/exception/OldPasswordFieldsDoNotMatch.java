package com.example.eventinformationsystembackend.exception;

public class OldPasswordFieldsDoNotMatch extends RuntimeException {
    public OldPasswordFieldsDoNotMatch(String message) {
        super(message);
    }
}
