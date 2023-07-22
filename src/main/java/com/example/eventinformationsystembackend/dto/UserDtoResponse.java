package com.example.eventinformationsystembackend.dto;

import com.example.eventinformationsystembackend.common.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDtoResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String email;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private String address;
    private String description;
    private String profilePicturePath;
    private UserRole userRole;
    private Boolean isLocked;
    private Boolean isEnabled;
}
