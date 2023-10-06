package com.example.eventinformationsystembackend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PasswordDto {
    @NotBlank
    private String oldPassword;

    @NotBlank
    private String confirmedOldPassword;

    @NotBlank
    private String newPassword;
}
