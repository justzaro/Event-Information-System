package com.example.eventinformationsystembackend.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SupportTicketDto {
    @NotBlank(message = "Subject field should not be blank")
    @Size(max = 255)
    private String subject;

    @NotBlank(message = "Description field should not be blank")
    @Size(max = 65535)
    private String description;

    @NotBlank(message = "First name field should not be blank")
    @Size(max = 255)
    private String customerFirstName;

    @NotBlank(message = "Last name field should not be blank")
    @Size(max = 255)
    private String customerLastName;

    @NotBlank(message = "Email field should not be blank")
    @Size(max = 255)
    private String customerEmail;

    @NotBlank(message = "Phone number field should not be blank")
    @Size(max = 255)
    private String customerPhoneNumber;
}
