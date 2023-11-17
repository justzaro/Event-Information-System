package com.example.eventinformationsystembackend.common;

public class ExceptionMessages {
    public static final String USER_DOES_NOT_EXIST = "Such user does not exist!";
    public static final String EVENT_DOES_NOT_EXIST = "Such event does not exist!";
    public static final String TOKEN_DOES_NOT_EXIST = "Such token does not exist!";
    public static final String POST_DOES_NOT_EXIST = "Such post does not exist!";
    public static final String CART_ITEM_DOES_NOT_EXIST = "Cart item with this ID does not exist!";
    public static final String COMMENT_DOES_NOT_EXIST = "Comment with this ID does not exist!";
    public static final String ARTIST_DOES_NOT_EXIST = "Artist with this ID does not exist!";

    public static final String RESOURCE_ACCESS_FORBIDDEN = "Access to this resource is forbidden!";
    public static final String ORDER_DOES_NOT_EXIST = "Order with this ID does not exist!";

    public static final String COUPON_IS_INVALID = "Invalid coupon code!";
    public static final String COUPON_HAS_EXPIRED = "Coupon code has expired!";
    public static final String COUPON_HAS_BEEN_USED = "Coupon has already been used!";
    public static final String COUPON_DOES_NOT_EXIST = "Coupon with this ID does not exist!";
    public static final String SUPPORT_TICKET_NOT_EXIST = "Support ticket with this ID does not exist!";

    public static final String POST_DOES_NOT_CONTAIN_IMAGE = "You must add an image to your post before you upload it!";

    public static final String TOKEN_HAS_EXPIRED = "Token has expired!";
    public static final String NOT_ENOUGH_SEATS = "Not enough seats for this concert";
    public static final String EMAIL_ALREADY_CONFIRMED = "Email is already confirmed";
    public static final String EMPTY_CART = "You must add items to your cart before you make an order!";
    public static final String CART_ITEM_TICKETS_EXCEED_EVENT_CAPACITY = "You must add items to your cart before you make an order!";


    public static final String USERNAME_ALREADY_EXISTS = "Desired username is already taken!";
    public static final String EMAIL_ALREADY_EXISTS = "Desired email is already taken!";
    public static final String EVENT_NAME_ALREADY_EXISTS = "Desired event name is already taken!";
    public static final String ARTIST_ALREADY_EXISTS = "Such artist already exists!";

    public static final String START_DATE_IS_AFTER_END_DATE =
            "Event start date is after end date!";
    public static final String EQUAL_START_AND_END_DATE =
            "Start date is equal to end date!";

    public static final String OLD_PASSWORD_FIELDS_DO_NOT_MATCH = "Old password and confirm old password fields do not match!";
    public static final String OLD_PASSWORD_MATCHES_NEW_PASSWORD = "Old password matches the new password!";

    public static final String WRONG_PASSWORD_EXCEPTION = "Wrong profile password!";
    public static final String ACCOUNT_NOT_ENABLED_EXCEPTION = "You account is not enabled! Please, do so from the link in your email or inform us via the support page!";
    public static final String ACCOUNT_LOCKED_EXCEPTION = "You account is locked by authorized staff!";
}
