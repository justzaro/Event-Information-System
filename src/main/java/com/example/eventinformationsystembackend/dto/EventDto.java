package com.example.eventinformationsystembackend.dto;

import com.example.eventinformationsystembackend.annotation.EnumValidatorConstraint;
import com.example.eventinformationsystembackend.common.enums.Currency;
import com.example.eventinformationsystembackend.model.Artist;
import com.example.eventinformationsystembackend.model.Order;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventDto {

    @NotBlank(message = "Name field should not be blank")
    @Size(max = 255)
    private String name;

    @NotBlank(message = "Description field should not be blank")
    @Size(max = 65535)
    private String description;

    @NotBlank(message = "Location field should not be blank")
    @Size(max = 255)
    private String location;

    @JsonFormat(pattern = "HH:mm dd-MM-yyyy")
    private LocalDateTime startDate;

    @JsonFormat(pattern = "HH:mm dd-MM-yyyy")
    private LocalDateTime endDate;

    @NotNull(message = "Capacity field must not be null!")
    private Integer capacity;

    @NotNull(message = "Is active field must not be null!")
    private Boolean isActive;

    //doesn't throw error message
    @NotNull(message = "Currency field must not be null!")
    //@EnumValidatorConstraint(enumClass = Currency.class, message = "Invalid currency type!")
    private Currency currency;

    @DecimalMin(value = "1", message = "Minimal ticket price should be 1")
    private Double ticketPrice;

    private Set<ArtistDtoResponse> artists;
}
