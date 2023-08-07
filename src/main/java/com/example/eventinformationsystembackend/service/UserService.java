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
import org.springframework.web.multipart.MultipartFile;

import static com.example.eventinformationsystembackend.common.ExceptionMessages.*;
import static com.example.eventinformationsystembackend.common.FilePaths.*;

import java.io.File;
import java.io.IOException;
import java.lang.module.ResolutionException;
import java.nio.file.Files;

@Service
public class UserService {
    private final String CONFIRMATION_LINK = "http://localhost:8080/users/confirm?token=";
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final ConfirmationTokenService confirmationTokenService;
    private final EmailService emailService;
    private final StorageService storageService;

    @Autowired
    public UserService(UserRepository userRepository,
                       ConfirmationTokenService confirmationTokenService,
                       EmailService emailService,
                       StorageService storageService) {
        this.userRepository = userRepository;
        this.modelMapper = new ModelMapper();
        this.confirmationTokenService = confirmationTokenService;
        this.emailService = emailService;
        this.storageService = storageService;
    }

    public UserDtoResponse getUser(String username) {
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(USER_DOES_NOT_EXIST));

        return modelMapper.map(user, UserDtoResponse.class);
    }

    public UserDtoResponse registerUser(UserDto userDto, MultipartFile profilePicture) {
        checkForDuplicateUsername(userDto.getUsername());
        checkForDuplicateEmail(userDto.getEmail());

        if (userDto.getPhoneNumber() != null) {
            if (!userDto.getPhoneNumber().isEmpty()) {
                checkForDuplicatePhoneNumber(userDto.getPhoneNumber());
            }
        }

        User userToRegister = modelMapper.map(userDto, User.class);

        String userFolderPath = USERS_FOLDER_PATH + userToRegister.getUsername();
        String userPostsFolderPath = userFolderPath + "\\" + "Posts";
        String userProfilePicturePath = userFolderPath + "\\" + profilePicture.getOriginalFilename();

        userToRegister.setUserRole(UserRole.USER);
        userToRegister.setIsEnabled(false);
        userToRegister.setIsLocked(false);
        userToRegister.setProfilePicturePath(userProfilePicturePath);

        storageService.createFolder(userFolderPath);
        storageService.createFolder(userPostsFolderPath);

        if (profilePicture != null) {
            if (!profilePicture.isEmpty()) {
                storageService.savePictureToFileSystem(profilePicture, userProfilePicturePath);
            }
        }

        userRepository.save(userToRegister);

        String confirmationToken =
                confirmationTokenService.createToken(userToRegister);
        String confirmationLink =
                CONFIRMATION_LINK + confirmationToken;

        emailService.sendConfirmationEmail(userToRegister, confirmationLink);

        return modelMapper.map(userToRegister, UserDtoResponse.class);
    }

    public UserDtoResponse updateUser(UserDto userDto, String username,
                                      MultipartFile profilePicture) {
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(USER_DOES_NOT_EXIST));

        if (!user.getUsername().equals(userDto.getUsername())) {
            checkForDuplicateUsername(userDto.getUsername());
        }

        if (!user.getEmail().equals(userDto.getEmail())) {
            checkForDuplicateEmail(userDto.getEmail());
        }

        if (userDto.getPhoneNumber() != null) {
            if (!user.getPhoneNumber().equals(userDto.getPhoneNumber())) {
                checkForDuplicatePhoneNumber(userDto.getPhoneNumber());
            }
        }

        String userFolderPath = USERS_FOLDER_PATH + user.getUsername();
        String renamedUserFolderPath = USERS_FOLDER_PATH + userDto.getUsername();

        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setUsername(userDto.getUsername());
        user.setPassword(userDto.getPassword());
        user.setEmail(userDto.getEmail());
        user.setPhoneNumber(userDto.getPhoneNumber());
        user.setDateOfBirth(userDto.getDateOfBirth());
        user.setAddress(userDto.getAddress());
        user.setDescription(userDto.getDescription());

        storageService.renameFolder(userFolderPath, renamedUserFolderPath);

        if (profilePicture != null) {
            if (!profilePicture.isEmpty()) {
                String userProfilePicturePath = renamedUserFolderPath + "\\"
                        + profilePicture.getOriginalFilename();

                storageService.savePictureToFileSystem(profilePicture, userProfilePicturePath);

                user.setProfilePicturePath(userProfilePicturePath);
            }
        }

        User updatedUser = userRepository.save(user);

        return modelMapper.map(updatedUser, UserDtoResponse.class);
    }

    public byte[] getUserProfilePicture(String username) throws IOException {
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(USER_DOES_NOT_EXIST));
        String profilePicturePath = user.getProfilePicturePath();
        byte[] profilePicture =
                Files.readAllBytes(new File(profilePicturePath).toPath());
        return profilePicture;
    }

    public void deleteUser(String username) {
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(USER_DOES_NOT_EXIST));

        String userFolderPath = USERS_FOLDER_PATH + user.getUsername();

        storageService.deleteFolder(userFolderPath);

        userRepository.delete(user);
    }

    private void checkForDuplicateUsername(String username) {
        if (userRepository.findUserByUsername(username).isPresent()) {
            throw new DuplicateUniqueFieldException(USERNAME_ALREADY_EXISTS);
        }
    }

    private void checkForDuplicateEmail(String email) {
        if (userRepository.findUserByEmail(email).isPresent()) {
            throw new DuplicateUniqueFieldException(EMAIL_ALREADY_EXISTS);
        }
    }

    private void checkForDuplicatePhoneNumber(String phoneNumber) {
        if (userRepository.findUserByPhoneNumber(phoneNumber).isPresent()) {
            throw new DuplicateUniqueFieldException(PHONE_NUMBER_ALREADY_EXISTS);
        }
    }
}
