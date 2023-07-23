package com.example.eventinformationsystembackend.dto;

import com.example.eventinformationsystembackend.annotation.EnumValidatorConstraint;
import com.example.eventinformationsystembackend.common.enums.Currency;
import com.example.eventinformationsystembackend.model.Order;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventDto {

    @NotBlank(message = "Name field should not be blank!")
    @Size(max = 255)
    private String name;

    @NotBlank(message = "Description field should not be blank!")
    @Size(max = 65535)
    private String description;

    @NotBlank(message = "Location field should not be blank!")
    @Size(max = 255)
    private String location;

    @JsonFormat(pattern = "HH:mm dd-MM-yyyy")
    private LocalDateTime startDate;

    @JsonFormat(pattern = "HH:mm dd-MM-yyyy")
    private LocalDateTime endDate;

    @EnumValidatorConstraint(enumClass = Currency.class, message = "Invalid currency type!")
    private Currency currency;

    @DecimalMin(value = "1", message = "Minimal ticket price should be 1!")
    private Double ticketPrice;
}
