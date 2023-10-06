package com.example.eventinformationsystembackend.dto;

import com.example.eventinformationsystembackend.common.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDtoResponse {
    @NotBlank
    private Long id;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @NotBlank
    private String email;

    @NotBlank
    private String phoneNumber;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate dateOfBirth;

    @NotBlank
    private String address;

    @NotBlank
    private String description;

    @NotBlank
    private String profilePicturePath;

    private UserRole userRole;

    private Boolean isLocked;

    private Boolean isEnabled;

    private List<SupportTicketDtoResponse> supportTickets;
}
