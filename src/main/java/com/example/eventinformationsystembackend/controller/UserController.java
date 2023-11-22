package com.example.eventinformationsystembackend.controller;

import com.example.eventinformationsystembackend.dto.PasswordDto;
import com.example.eventinformationsystembackend.dto.UserDto;
import com.example.eventinformationsystembackend.dto.UserDtoResponse;
import com.example.eventinformationsystembackend.dto.UserUpdateDto;
import com.example.eventinformationsystembackend.service.implementation.ConfirmationTokenServiceImpl;
import com.example.eventinformationsystembackend.service.implementation.UserServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserServiceImpl userServiceImpl;
    private final ConfirmationTokenServiceImpl confirmationTokenServiceImpl;

    @Autowired
    public UserController(UserServiceImpl userServiceImpl,
                          ConfirmationTokenServiceImpl confirmationTokenServiceImpl) {
        this.userServiceImpl = userServiceImpl;
        this.confirmationTokenServiceImpl = confirmationTokenServiceImpl;
    }

    @GetMapping("/confirmation")
    public RedirectView confirmToken(@RequestParam String token) {
        String confirmationStatus = "success";

        if (!confirmationTokenServiceImpl.confirmToken(token)) {
            confirmationStatus = "failure";
        }

        String redirectUrl =
                "http://localhost:3000/log-in?confirmationStatus=" + confirmationStatus;

        return new RedirectView(redirectUrl);
    }

    @GetMapping
    public List<UserDtoResponse> getAllUsers() {
        return userServiceImpl.getAllUsers();
    }

    @GetMapping("/{username}")
    public UserDtoResponse getUser(@PathVariable String username) {
        return userServiceImpl.getUser(username);
    }

    @GetMapping("/profile-picture/{username}")
    public ResponseEntity<?> getUserProfilePicture(
            @PathVariable String username) throws IOException {
        byte[] profilePicture = userServiceImpl.getUserProfilePicture(username);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf("image/png"))
                .body(profilePicture);
    }

    @PostMapping
    public UserDtoResponse registerUser(@RequestBody @Valid UserDto userDto) {
        return userServiceImpl.registerUser(userDto);
    }

    @PatchMapping("/password/{username}")
    public ResponseEntity<?> changePassword(@PathVariable String username,
                                            @Valid @RequestBody PasswordDto passwordDto) {
        userServiceImpl.changePassword(username, passwordDto);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/profile-picture/default/{username}")
    public ResponseEntity<?> resetProfilePictureToDefault(@PathVariable String username) {
        userServiceImpl.resetProfilePictureToDefault(username);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{username}")
    public UserDtoResponse updateUser(@PathVariable String username,
                                      @RequestPart @Valid UserUpdateDto userUpdateDto,
                                      @RequestPart(required = false) MultipartFile profilePicture) {
        return userServiceImpl.updateUser(userUpdateDto, username, profilePicture);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{username}/enabled")
    public ResponseEntity<Void> toggleUserEnabledStatus(@PathVariable String username) {
        userServiceImpl.toggleUserEnabledStatus(username);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{username}/locked")
    public ResponseEntity<Void> toggleUserLockedStatus(@PathVariable("username") String username) {
        userServiceImpl.toggleUserLockedStatus(username);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(path = "/{username}")
    public ResponseEntity<Void> deleteUser(@PathVariable("username") String username) {
        userServiceImpl.deleteUser(username);
        return ResponseEntity.noContent().build();
    }
}
