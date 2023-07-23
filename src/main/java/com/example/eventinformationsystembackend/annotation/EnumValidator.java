package com.example.eventinformationsystembackend.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EnumValidator implements ConstraintValidator<EnumValidatorConstraint, String> {
    List<String> itemTypeList;

    @Override
    public void initialize(EnumValidatorConstraint constraintAnnotation) {
        itemTypeList = Stream.of(constraintAnnotation.enumClass().getEnumConstants()).map(Enum::name).collect(Collectors.toList());
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return itemTypeList.contains(s);
    }
}
