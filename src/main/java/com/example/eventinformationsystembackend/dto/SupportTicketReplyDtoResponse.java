package com.example.eventinformationsystembackend.dto;

import com.example.eventinformationsystembackend.model.SupportTicket;
import com.example.eventinformationsystembackend.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
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
public class SupportTicketReplyDtoResponse {
    @NotBlank
    private String text;

    @NotBlank
    private LocalDateTime createdAt;

//    private UserDtoResponse replier;
    private String replierUsername;
}
