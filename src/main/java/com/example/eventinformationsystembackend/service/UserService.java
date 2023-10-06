package com.example.eventinformationsystembackend.service;

import com.example.eventinformationsystembackend.common.enums.UserRole;
import com.example.eventinformationsystembackend.dto.PasswordDto;
import com.example.eventinformationsystembackend.dto.UserDto;
import com.example.eventinformationsystembackend.dto.UserDtoResponse;
import com.example.eventinformationsystembackend.exception.*;
import com.example.eventinformationsystembackend.model.Post;
import com.example.eventinformationsystembackend.model.User;
import com.example.eventinformationsystembackend.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;
import org.springframework.web.multipart.MultipartFile;

import static com.example.eventinformationsystembackend.common.ExceptionMessages.*;
import static com.example.eventinformationsystembackend.common.FilePaths.*;

import java.io.File;
import java.io.IOException;

import java.nio.file.Files;

@Service
public class UserService {
    private final String CONFIRMATION_LINK = "http://localhost:8080/users/confirm?token=";
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final ConfirmationTokenService confirmationTokenService;
    private final EmailService emailService;
    private final StorageService storageService;
    private final PostService postService;

    @Autowired
    public UserService(UserRepository userRepository,
                       ConfirmationTokenService confirmationTokenService,
                       EmailService emailService,
                       StorageService storageService,
                       PostService postService) {
        this.userRepository = userRepository;
        this.modelMapper = new ModelMapper();
        this.confirmationTokenService = confirmationTokenService;
        this.emailService = emailService;
        this.storageService = storageService;
        this.postService = postService;
    }

    public UserDtoResponse getUser(String username) {
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(USER_DOES_NOT_EXIST));

        return modelMapper.map(user, UserDtoResponse.class);
    }

    public UserDtoResponse registerUser(UserDto userDto) {
        checkForDuplicateUsername(userDto.getUsername());
        checkForDuplicateEmail(userDto.getEmail());

/*        if (userDto.getPhoneNumber() != null) {
            if (!userDto.getPhoneNumber().isEmpty()) {
                checkForDuplicatePhoneNumber(userDto.getPhoneNumber());
            }
        }*/

        User userToRegister = modelMapper.map(userDto, User.class);

        String userFolderPath = USERS_FOLDER_PATH + userToRegister.getUsername();
        String userPostsFolderPath = userFolderPath + "\\" + "Posts";
//        String userProfilePicturePath = userFolderPath + "\\" + profilePicture.getOriginalFilename();
        String userProfilePicturePath = DEFAULT_USER_PROFILE_PICTURE;


        userToRegister.setUserRole(UserRole.USER);
        userToRegister.setIsEnabled(false);
        userToRegister.setIsLocked(false);
        userToRegister.setProfilePicturePath(userProfilePicturePath);
        userToRegister.setProfilePictureName("default_user_profile_picture.png");

        storageService.createFolder(userFolderPath);
        storageService.createFolder(userPostsFolderPath);

//        if (profilePicture != null) {
//            if (!profilePicture.isEmpty()) {
//                userToRegister.setProfilePicturePath(userProfilePicturePath);
//                userToRegister.setProfilePictureName(profilePicture.getOriginalFilename());
//                storageService.savePictureToFileSystem(profilePicture, userProfilePicturePath);
//            }
//        }


        userRepository.save(userToRegister);

        String confirmationToken =
                confirmationTokenService.createToken(userToRegister);
        String confirmationLink =
                CONFIRMATION_LINK + confirmationToken;

        emailService.sendConfirmationEmail(userToRegister, confirmationLink);

        return modelMapper.map(userToRegister, UserDtoResponse.class);
    }

    public void resetProfilePictureToDefault(String username) {
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(USER_DOES_NOT_EXIST));

        user.setProfilePicturePath(DEFAULT_USER_PROFILE_PICTURE);
        user.setProfilePictureName(DEFAULT_USER_PROFILE_PICTURE_NAME);
        userRepository.save(user);
    }

    public UserDtoResponse updateUser(UserDto userDto, String username,
                                      MultipartFile profilePicture) {
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(USER_DOES_NOT_EXIST));

        String currentUserFolderPath = USERS_FOLDER_PATH + user.getUsername();
        String newUserFolderPath = USERS_FOLDER_PATH + user.getUsername();

        if (!user.getUsername().equals(userDto.getUsername())) {
            checkForDuplicateUsername(userDto.getUsername());
            newUserFolderPath = USERS_FOLDER_PATH + userDto.getUsername();
            storageService.renameFolder(currentUserFolderPath, newUserFolderPath);

//            if (user.getProfilePicturePath() != null) {
//                String newUserProfilePicturePath = newUserFolderPath + "\\" + user.getProfilePictureName();
//                user.setProfilePicturePath(newUserProfilePicturePath);
//            }
            if (profilePicture != null) {
                String newUserProfilePicturePath = newUserFolderPath + "\\" + user.getProfilePictureName();
                user.setProfilePicturePath(newUserProfilePicturePath);
            }

            postService.replaceOldUsernameWithNewOneInPicturePathForAllUserPosts(username,
                    userDto.getUsername());
        }

        if (!user.getEmail().equals(userDto.getEmail())) {
            checkForDuplicateEmail(userDto.getEmail());
        }

/*        if (userDto.getPhoneNumber() != null) {
            if (!user.getPhoneNumber().equals(userDto.getPhoneNumber())) {
                checkForDuplicatePhoneNumber(userDto.getPhoneNumber());
            }
        }*/

        if (profilePicture != null && !profilePicture.isEmpty()) {
            if (user.getProfilePicturePath() != null
            && !user.getProfilePictureName().equals(DEFAULT_USER_PROFILE_PICTURE_NAME)) {
                storageService.deleteFile(user.getProfilePicturePath());
            }

            String newUserProfilePicturePath = newUserFolderPath + "\\"
                    + profilePicture.getOriginalFilename();

            storageService.savePictureToFileSystem(profilePicture, newUserProfilePicturePath);

            user.setProfilePicturePath(newUserProfilePicturePath);
            user.setProfilePictureName(profilePicture.getOriginalFilename());
        }

        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setUsername(userDto.getUsername());
        //user.setPassword(userDto.getPassword());
        user.setEmail(userDto.getEmail());
        //user.setPhoneNumber(userDto.getPhoneNumber());
        user.setDateOfBirth(userDto.getDateOfBirth());
        user.setAddress(userDto.getAddress());
        //user.setDescription(userDto.getDescription());

        User updatedUser = userRepository.save(user);

        return modelMapper.map(updatedUser, UserDtoResponse.class);
    }

    public byte[] getUserProfilePicture(String username) throws IOException {
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(USER_DOES_NOT_EXIST));
        String profilePicturePath = user.getProfilePicturePath();
        return Files.readAllBytes(new File(profilePicturePath).toPath());
    }

    public void changePassword(String username, PasswordDto passwordDto) {
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(USER_DOES_NOT_EXIST));

        if (!passwordDto.getOldPassword().equals(passwordDto.getConfirmedOldPassword())) {
            throw new OldPasswordFieldsDoNotMatch(OLD_PASSWORD_FIELDS_DO_NOT_MATCH);
        }

        if (!user.getPassword().equals(passwordDto.getOldPassword())) {
            throw new WrongPasswordException(WRONG_PASSWORD_EXCEPTION);
        }

        if (passwordDto.getOldPassword().equals(passwordDto.getNewPassword())) {
            throw new OldPasswordMatchesNewPassword(OLD_PASSWORD_MATCHES_NEW_PASSWORD);
        }

        user.setPassword(passwordDto.getNewPassword());
        userRepository.save(user);
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
