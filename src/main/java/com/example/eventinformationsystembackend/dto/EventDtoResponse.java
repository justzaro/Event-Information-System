package com.example.eventinformationsystembackend.dto;

import com.example.eventinformationsystembackend.common.enums.Currency;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventDtoResponse {

    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotBlank
    private String location;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private Currency currency;

    private Double ticketPrice;

    @NotBlank
    private String eventPicturePath;
}
