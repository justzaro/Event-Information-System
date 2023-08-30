package com.example.eventinformationsystembackend.dto;

public interface CartItemDtoResponseProjection {

    long getCartItemId();
    int getTicketQuantity();
    double getTotalPrice();
    double getTicketPrice();
    String getEventPicturePath();
    String getEventName();
}
