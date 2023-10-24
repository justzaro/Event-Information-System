package com.example.eventinformationsystembackend.annotation;

import com.example.eventinformationsystembackend.common.enums.EventType;
import com.example.eventinformationsystembackend.dto.EventDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
public class TicketPriceValidator implements ConstraintValidator<TicketPriceConstraint, EventDto> {
    @Override
    public boolean isValid(EventDto dto, ConstraintValidatorContext context) {
        if (dto.getEventType().equals(EventType.FESTIVAL)) {
            // Allow a ticketPrice of 0 for the Festival eventType
            return dto.getTicketPrice() >= 0;
        } else {
            // For other event types, ensure that the ticketPrice is at least 1
            return dto.getTicketPrice() >= 1;
        }
    }
}
