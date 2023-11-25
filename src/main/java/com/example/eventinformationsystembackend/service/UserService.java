package com.example.eventinformationsystembackend.service;

import com.example.eventinformationsystembackend.dto.PasswordDto;
import com.example.eventinformationsystembackend.dto.UserDto;
import com.example.eventinformationsystembackend.dto.UserDtoResponse;
import com.example.eventinformationsystembackend.dto.UserUpdateDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserService {
    void changePassword(String username, PasswordDto passwordDto);

    void deleteUser(String username);

    void toggleUserEnabledStatus(String username);

    void toggleUserLockedStatus(String username);

    void resetProfilePictureToDefault(String username);

    byte[] getUserProfilePicture(String username) throws IOException;

    UserDtoResponse getUser(String username);

    UserDtoResponse registerUser(UserDto userDto);

    UserDtoResponse updateUser(UserUpdateDto userUpdateDto, String username,
                               MultipartFile profilePicture);

    List<UserDtoResponse> getAllUsers();
}
