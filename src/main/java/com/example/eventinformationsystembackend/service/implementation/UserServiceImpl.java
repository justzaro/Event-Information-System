package com.example.eventinformationsystembackend.service.implementation;

import com.example.eventinformationsystembackend.common.enums.UserRole;
import com.example.eventinformationsystembackend.dto.*;
import com.example.eventinformationsystembackend.exception.*;
import com.example.eventinformationsystembackend.model.User;
import com.example.eventinformationsystembackend.repository.UserRepository;

import com.example.eventinformationsystembackend.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;
import org.springframework.web.multipart.MultipartFile;

import static com.example.eventinformationsystembackend.common.ExceptionMessages.*;
import static com.example.eventinformationsystembackend.common.FilePaths.*;
import static com.example.eventinformationsystembackend.common.UserInformation.*;

import java.io.File;
import java.io.IOException;

import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper = new ModelMapper();
    private final ConfirmationTokenService confirmationTokenService;
    private final EmailService emailService;
    private final StorageService storageService;
    private final PostService postService;
    private final DataValidationService dataValidationService;

    public UserDtoResponse getUser(String username) {
        User user = findUser(username);
        return modelMapper.map(user, UserDtoResponse.class);
    }

    public List<UserDtoResponse> getAllUsers() {
        List<User> users = userRepository.findAll();

        return users
               .stream()
               .map(user -> modelMapper.map(user, UserDtoResponse.class))
               .collect(Collectors.toList());
    }

    public UserDtoResponse registerUser(UserDto userDto) {
        checkForDuplicateUsername(userDto.getUsername());
        checkForDuplicateEmail(userDto.getEmail());

        User userToRegister = modelMapper.map(userDto, User.class);

        createUserLocalFolder(userToRegister);
        setUserProfileSettings(userToRegister);

        modelMapper.map(userDto, userToRegister);

        userRepository.save(userToRegister);

        sendProfileConfirmationEmail(userToRegister);

        return modelMapper.map(userToRegister, UserDtoResponse.class);
    }

    public void resetProfilePictureToDefault(String username) {
        User user = findUser(username);

        user.setProfilePicturePath(DEFAULT_USER_PROFILE_PICTURE);
        user.setProfilePictureName(DEFAULT_USER_PROFILE_PICTURE_NAME);

        userRepository.save(user);
    }

    public UserDtoResponse updateUser(UserUpdateDto userUpdateDto, String username,
                                      MultipartFile profilePicture) {
        User user = findUser(username);

        if (!user.getEmail().equals(userUpdateDto.getEmail())) {
            checkForDuplicateEmail(userUpdateDto.getEmail());
        }

        String currentUserFolderPath = USERS_FOLDER_PATH + user.getUsername();
        String newUserFolderPath = USERS_FOLDER_PATH + user.getUsername();

        renameUserFolderInCaseOfNewUsername(user, userUpdateDto, currentUserFolderPath,
                                            profilePicture);
        replaceOldProfilePictureWithNewOne(profilePicture, user, newUserFolderPath);

        modelMapper.map(userUpdateDto, user);
        user = userRepository.save(user);

        return modelMapper.map(user, UserDtoResponse.class);
    }

    public byte[] getUserProfilePicture(String username) throws IOException {
        User user = findUser(username);
        String profilePicturePath = user.getProfilePicturePath();
        return Files.readAllBytes(new File(profilePicturePath).toPath());
    }

    public void changePassword(String username, PasswordDto passwordDto) {
        User user = findUser(username);

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
        User user = findUser(username);

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

    public void toggleUserEnabledStatus(String username) {
        User user = findUser(username);
        user.setIsEnabled(!user.getIsEnabled());
        userRepository.save(user);
    }

    public void toggleUserLockedStatus(String username) {
        User user = findUser(username);
        user.setIsLocked(!user.getIsLocked());
        userRepository.save(user);
    }

    private User findUser(String username) {
        return dataValidationService.getUserByUsername(username);
    }

    private void renameUserFolderInCaseOfNewUsername(User user, UserUpdateDto userUpdateDto,
                                                     String currentUserFolderPath,
                                                     MultipartFile profilePicture) {
        if (!user.getUsername().equals(userUpdateDto.getUsername())) {
            checkForDuplicateUsername(userUpdateDto.getUsername());
            String newUserFolderPath = USERS_FOLDER_PATH + userUpdateDto.getUsername();
            storageService.renameFolder(currentUserFolderPath, newUserFolderPath);

            if (profilePicture != null) {
                String newUserProfilePicturePath = newUserFolderPath + "\\" + user.getProfilePictureName();
                user.setProfilePicturePath(newUserProfilePicturePath);
            }

            postService.replaceOldUsernameWithNewOneInPicturePathForAllUserPosts(user.getUsername(),
                    userUpdateDto.getUsername());
        }
    }

    private void replaceOldProfilePictureWithNewOne(MultipartFile profilePicture, User user,
                                                    String newUserFolderPath) {
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
    }

    private void createUserLocalFolder(User userToRegister) {
        String userFolderPath = USERS_FOLDER_PATH + userToRegister.getUsername();
        String userPostsFolderPath = userFolderPath + "\\" + "Posts";

        storageService.createFolder(userFolderPath);
        storageService.createFolder(userPostsFolderPath);
    }

    private void setUserProfileSettings(User userToRegister) {
        userToRegister.setUserRole(UserRole.USER);
        userToRegister.setIsEnabled(false);
        userToRegister.setIsLocked(false);
        userToRegister.setProfilePicturePath(DEFAULT_USER_PROFILE_PICTURE);
        userToRegister.setProfilePictureName(DEFAULT_USER_PROFILE_PICTURE_NAME);
    }

    private void sendProfileConfirmationEmail(User userToRegister) {
        String confirmationToken =
                confirmationTokenService.createToken(userToRegister);
        String confirmationLink =
                CONFIRMATION_LINK + confirmationToken;

        emailService.sendConfirmationEmail(userToRegister, confirmationLink);
    }
}
