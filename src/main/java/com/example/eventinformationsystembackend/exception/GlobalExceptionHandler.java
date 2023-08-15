package com.example.eventinformationsystembackend.exception;

import com.example.eventinformationsystembackend.annotation.EnumValidatorConstraint;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        ErrorResponse errorResponse = new ErrorResponse();

        errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        errorResponse.setMessage(e.getBindingResult().getAllErrors().get(0).getDefaultMessage() + "!");
        errorResponse.setTimestamp(LocalDateTime.now());

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<ErrorResponse> handleDateTimeParseException(DateTimeParseException e) {
        ErrorResponse errorResponse = new ErrorResponse();

        errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        errorResponse.setMessage("Enter date in the following format: {HH:mm dd-MM-yyyy}!");
        errorResponse.setTimestamp(LocalDateTime.now());

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException e) {
        ErrorResponse errorResponse = new ErrorResponse();

        errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
        errorResponse.setMessage(e.getMessage());
        errorResponse.setTimestamp(LocalDateTime.now());

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({DuplicateUniqueFieldException.class,
                       NotEnoughSeatsException.class})
    public ResponseEntity<ErrorResponse> handleDuplicateUniqueFieldException(Exception e) {
        ErrorResponse errorResponse = new ErrorResponse();

        errorResponse.setStatus(HttpStatus.CONFLICT.value());
        errorResponse.setMessage(e.getMessage());
        errorResponse.setTimestamp(LocalDateTime.now());

        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler({InvalidEventDateException.class,
                       CouponHasExpiredException.class,
                       CouponHasBeenUsedException.class,
                       EmailAlreadyConfirmedException.class})
    public ResponseEntity<ErrorResponse> handleInvalidEventDateException(Exception e) {
        ErrorResponse errorResponse = new ErrorResponse();

        errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        errorResponse.setMessage(e.getMessage());
        errorResponse.setTimestamp(LocalDateTime.now());

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    //add resource not found exception, then make more normal exception
    //ie usernotfound which implement it
}
