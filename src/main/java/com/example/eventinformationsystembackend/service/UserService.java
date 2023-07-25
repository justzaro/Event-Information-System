package com.example.eventinformationsystembackend.service;

import com.example.eventinformationsystembackend.common.enums.UserRole;
import com.example.eventinformationsystembackend.dto.UserDto;
import com.example.eventinformationsystembackend.dto.UserDtoResponse;
import com.example.eventinformationsystembackend.exception.DuplicateUniqueFieldException;
import com.example.eventinformationsystembackend.exception.ResourceNotFoundException;
import com.example.eventinformationsystembackend.model.User;
import com.example.eventinformationsystembackend.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;
import org.springframework.ui.Model;
import static com.example.eventinformationsystembackend.common.ExceptionMessages.*;
import static com.example.eventinformationsystembackend.common.FilePaths.*;

import java.io.File;
import java.lang.module.ResolutionException;

@Service
public class UserService {
    private final String CONFIRMATION_LINK = "http://localhost:8080/users/confirm?token=";
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final ConfirmationTokenService confirmationTokenService;
    private final EmailService emailService;


    @Autowired
    public UserService(UserRepository userRepository,
                       ConfirmationTokenService confirmationTokenService,
                       EmailService emailService) {
        this.userRepository = userRepository;
        this.modelMapper = new ModelMapper();
        this.confirmationTokenService = confirmationTokenService;
        this.emailService = emailService;
    }

    public UserDtoResponse getUser(String username) {
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(USER_DOES_NOT_EXIST));

        return modelMapper.map(user, UserDtoResponse.class);
    }

    public UserDtoResponse registerUser(UserDto userDto) {
        if (userRepository.findUserByUsername(userDto.getUsername()).isPresent()) {
            throw new DuplicateUniqueFieldException(USERNAME_ALREADY_EXISTS);
        }

        if (userRepository.findUserByEmail(userDto.getUsername()).isPresent()) {
            throw new DuplicateUniqueFieldException(EMAIL_ALREADY_EXISTS);
        }

        if (userRepository.findUserByPhoneNumber(userDto.getUsername()).isPresent()) {
            throw new DuplicateUniqueFieldException(PHONE_NUMBER_ALREADY_EXISTS);
        }

        User userToRegister = modelMapper.map(userDto, User.class);

        userToRegister.setUserRole(UserRole.USER);
        userToRegister.setIsEnabled(false);
        userToRegister.setIsLocked(false);
        userToRegister.setProfilePicturePath("asd");

        new File(USERS_FOLDER_PATH + userToRegister.getUsername()).mkdirs();

        userRepository.save(userToRegister);

        String confirmationToken =
                confirmationTokenService.createToken(userToRegister);
        String confirmationLink =
                CONFIRMATION_LINK + confirmationToken;

        emailService.sendConfirmationEmail(userToRegister, confirmationLink);

        return modelMapper.map(userToRegister, UserDtoResponse.class);
    }

    private void createUserFolder(User userToRegister) {
        new File(USERS_FOLDER_PATH + userToRegister.getUsername()).mkdirs();
    }

    private void sendConfirmationLink(User userToRegister, String confirmationLink) {
        emailService.sendConfirmationEmail(userToRegister, confirmationLink);
    }

}
