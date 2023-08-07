package com.example.eventinformationsystembackend.common;

public class ExceptionMessages {
    public static final String USER_DOES_NOT_EXIST = "Such user does not exist!";
    public static final String EVENT_DOES_NOT_EXIST = "Such event does not exist!";
    public static final String TOKEN_DOES_NOT_EXIST = "Such token does not exist!";
    public static final String POST_DOES_NOT_EXIST = "Such post does not exist!";

    public static final String TOKEN_HAS_EXPIRED = "Token has expired!";
    public static final String NOT_ENOUGH_SEATS = "Not enough seats for this concert";

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
