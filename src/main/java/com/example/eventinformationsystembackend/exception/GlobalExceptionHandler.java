package com.example.eventinformationsystembackend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
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
    public ResponseEntity<ErrorResponse> handleNotFoundExceptions(ResourceNotFoundException e) {
        ErrorResponse errorResponse = new ErrorResponse();

        errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
        errorResponse.setMessage(e.getMessage());
        errorResponse.setTimestamp(LocalDateTime.now());

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({
            DuplicateUniqueFieldException.class,
            OldPasswordMatchesNewPassword.class,
            NotEnoughSeatsException.class
    })
    public ResponseEntity<ErrorResponse> handleConflictExceptions(Exception e) {
        ErrorResponse errorResponse = new ErrorResponse();

        errorResponse.setStatus(HttpStatus.CONFLICT.value());
        errorResponse.setMessage(e.getMessage());
        errorResponse.setTimestamp(LocalDateTime.now());

        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler({
            InvalidCreditCardCredentialsException.class,
            CartItemTicketsExceedEventCapacity.class,
            PostDoesNotContainImageException.class,
            EmailAlreadyConfirmedException.class,
            OldPasswordFieldsDoNotMatch.class,
            CouponHasBeenUsedException.class,
            InvalidEventDateException.class,
            CouponHasExpiredException.class,
            BadCredentialsException.class,
            WrongPasswordException.class,
            EmptyCartException.class,
            DisabledException.class,
            LockedException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequestExceptions(Exception e) {
        ErrorResponse errorResponse = new ErrorResponse();

        errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        errorResponse.setMessage(e.getMessage());
        errorResponse.setTimestamp(LocalDateTime.now());

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ForbiddenException.class})
    public ResponseEntity<ErrorResponse> handleForbiddenException(Exception e) {
        ErrorResponse errorResponse = new ErrorResponse();

        errorResponse.setStatus(HttpStatus.FORBIDDEN.value());
        errorResponse.setMessage(e.getMessage());
        errorResponse.setTimestamp(LocalDateTime.now());

        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }
}
