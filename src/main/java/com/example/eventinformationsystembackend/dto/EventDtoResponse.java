package com.example.eventinformationsystembackend.dto;

import com.example.eventinformationsystembackend.common.enums.Currency;
import com.example.eventinformationsystembackend.common.enums.EventType;
import com.example.eventinformationsystembackend.common.enums.UserRole;
import com.example.eventinformationsystembackend.model.Artist;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class EventDtoResponse {

    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotBlank
    private String location;

    private EventType eventType;

    @JsonFormat(pattern = "HH:mm dd/MM/yyyy")
    private LocalDateTime startDate;

    @JsonFormat(pattern = "HH:mm dd/MM/yyyy")
    private LocalDateTime endDate;

    @NotNull
    private Integer capacity;

    @NotNull
    private Boolean isActive;

    private Currency currency;

    private Double ticketPrice;

    @NotBlank
    private String eventPicturePath;

    private List<ArtistDtoResponse> artists;
}
