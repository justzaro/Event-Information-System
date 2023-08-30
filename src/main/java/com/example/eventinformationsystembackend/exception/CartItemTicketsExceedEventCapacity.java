package com.example.eventinformationsystembackend.exception;

public class CartItemTicketsExceedEventCapacity extends RuntimeException {
    public CartItemTicketsExceedEventCapacity(String message) {
        super(message);
    }
}
