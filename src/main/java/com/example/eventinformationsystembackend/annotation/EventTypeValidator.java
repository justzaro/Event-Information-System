package com.example.eventinformationsystembackend.annotation;

import com.example.eventinformationsystembackend.common.enums.EventType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EventTypeValidator implements ConstraintValidator<ValidEventType, EventType> {
    @Override
    public void initialize(ValidEventType constraintAnnotation) {
    }

    @Override
    public boolean isValid(EventType value, ConstraintValidatorContext context) {
        return value == EventType.FESTIVAL || value == EventType.CONCERT;
    }
}
