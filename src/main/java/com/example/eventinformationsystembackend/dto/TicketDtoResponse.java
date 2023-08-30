package com.example.eventinformationsystembackend.dto;

import com.example.eventinformationsystembackend.model.Event;
import com.example.eventinformationsystembackend.model.OrderItem;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TicketDtoResponse {

    @NotNull
    private String code;

    @NotNull
    private String qrCodeImagePath;

    @NotNull
    private LocalDateTime createAt;

    @NotNull
    private Boolean isChecked;

    @NotNull
    private EventDtoResponse event;

    @Override
    public String toString() {
        return "TicketDtoResponse{" +
                "code='" + code + '\'' +
                ", qrCodeImagePath='" + qrCodeImagePath + '\'' +
                ", createAt=" + createAt +
                ", isChecked=" + isChecked +
                ", event=" + event +
                '}';
    }
}
