package com.example.eventinformationsystembackend.common;

public class ExceptionMessages {
    public static final String USER_DOES_NOT_EXIST = "Such user does not exist!";
    public static final String EVENT_DOES_NOT_EXIST = "Such event does not exist!";
    public static final String TOKEN_DOES_NOT_EXIST = "Such token does not exist!";
    public static final String POST_DOES_NOT_EXIST = "Such post does not exist!";
    public static final String CART_ITEM_DOES_NOT_EXIST = "Cart item with this ID does not exist!";

    public static final String ORDER_DOES_NOT_EXIST = "Order with this ID does not exist!";

    public static final String COUPON_IS_INVALID = "Invalid coupon code!";
    public static final String COUPON_HAS_EXPIRED = "Coupon code has expired!";
    public static final String COUPON_HAS_BEEN_USED = "Coupon has already been used!";

    public static final String POST_DOES_NOT_CONTAIN_IMAGE = "You must add an image to your post before you upload it!";

    public static final String TOKEN_HAS_EXPIRED = "Token has expired!";
    public static final String NOT_ENOUGH_SEATS = "Not enough seats for this concert";
    public static final String EMAIL_ALREADY_CONFIRMED = "Email is already confirmed";
    public static final String EMPTY_CART = "You must add items to your cart before you make an order!";
    public static final String CART_ITEM_TICKETS_EXCEED_EVENT_CAPACITY = "You must add items to your cart before you make an order!";


    public static final String USERNAME_ALREADY_EXISTS = "Desired username is already taken!";
    public static final String EMAIL_ALREADY_EXISTS = "Desired email is already taken!";
    public static final String PHONE_NUMBER_ALREADY_EXISTS = "Desired phone number is already taken!";
    public static final String EVENT_NAME_ALREADY_EXISTS = "Desired event name is already taken!";
    public static final String ARTIST_ALREADY_EXISTS = "Such artist already exists!";

    public static final String START_DATE_IS_AFTER_END_DATE =
            "Event start date is after end date!";
    public static final String EQUAL_START_AND_END_DATE =
            "Start date is equal to end date!";
}
