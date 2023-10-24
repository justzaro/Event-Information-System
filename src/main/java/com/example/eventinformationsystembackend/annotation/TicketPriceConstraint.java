package com.example.eventinformationsystembackend.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TicketPriceValidator.class)
@Documented
public @interface TicketPriceConstraint {
    String message() default "Minimal ticket price should be 1";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}