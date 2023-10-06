package com.example.eventinformationsystembackend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDto {
    @NotBlank(message = "First name field should not be blank")
    @Size(max = 255)
    private String firstName;

    @NotBlank(message = "Last name field should not be blank")
    @Size(max = 255)
    private String lastName;

    @NotBlank(message = "Username field should not be blank")
    @Size(max = 255)
    private String username;

    @NotBlank(message = "Email field should not be blank")
    @Email(message = "Enter a valid email address")
    @Size(max = 255)
    private String email;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate dateOfBirth;

    @Size(max = 255)
    private String address;
}
