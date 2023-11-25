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

    @NotBlank(message = "Credit card number field should not be blank")
    @Size(max = 16, message = "Credit card number should not be longer than 16 digits")
    private String creditCardNumber;

    @NotBlank(message = "Credit card number field should not be blank")
    @Size(max = 3, message = "CVV number should be 3 digits long")
    private String creditCardCvv;
}
