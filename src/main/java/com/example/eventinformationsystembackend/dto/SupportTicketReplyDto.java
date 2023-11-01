package com.example.eventinformationsystembackend.dto;

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
public class SupportTicketReplyDto {
    @NotBlank(message = "Reply text textarea should not be blank!")
    private String text;

    private SupportTicketDtoResponse supportTicket;
}
