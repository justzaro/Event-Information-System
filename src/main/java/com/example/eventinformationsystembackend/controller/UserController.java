package com.example.eventinformationsystembackend.controller;

import com.example.eventinformationsystembackend.dto.PasswordDto;
import com.example.eventinformationsystembackend.dto.UserDto;
import com.example.eventinformationsystembackend.dto.UserDtoResponse;
import com.example.eventinformationsystembackend.dto.UserUpdateDto;
import com.example.eventinformationsystembackend.service.ConfirmationTokenService;
import com.example.eventinformationsystembackend.service.UserService;
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
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;
    private final ConfirmationTokenService confirmationTokenService;

    @Autowired
    public UserController(UserService userService,
                          ConfirmationTokenService confirmationTokenService) {
        this.userService = userService;
        this.confirmationTokenService = confirmationTokenService;
    }

    @GetMapping(path = "/confirm")
    public RedirectView confirmToken(@RequestParam String token) {
        String confirmationStatus = "success";

        if (!confirmationTokenService.confirmToken(token)) {
            confirmationStatus = "failure";
        }

        String redirectUrl =
                "http://localhost:3000/log-in?confirmationStatus=" + confirmationStatus;

        return new RedirectView(redirectUrl);
    }

    @GetMapping(path = "/{username}")
    public UserDtoResponse getUser(@PathVariable("username") String username) {
        return userService.getUser(username);
    }

    @GetMapping
    public List<UserDtoResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping(path = "/profile-picture/{username}")
    public ResponseEntity<?> getUserProfilePicture(
            @PathVariable("username") String username) throws IOException {
        byte[] profilePicture = userService.getUserProfilePicture(username);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf("image/png"))
                .body(profilePicture);
    }

    @PostMapping(path = "/register")
    public UserDtoResponse registerUser(@RequestBody @Valid UserDto userDto) {
        return userService.registerUser(userDto);
    }

    @PatchMapping(path = "/password/{username}")
    public ResponseEntity<?> changePassword(@PathVariable("username") String username,
                                            @Valid @RequestBody PasswordDto passwordDto) {
        userService.changePassword(username, passwordDto);
        return ResponseEntity.ok().build();
    }

    @PatchMapping(path = "/profile-picture/default/{username}")
    public ResponseEntity<?> resetProfilePictureToDefault(@PathVariable("username") String username) {
        userService.resetProfilePictureToDefault(username);
        return ResponseEntity.ok().build();
    }

    @PutMapping(path = "/{username}")
    public UserDtoResponse updateUser(@PathVariable("username") String username,
                                      @RequestPart @Valid UserUpdateDto userUpdateDto,
                                      @RequestPart(required = false) MultipartFile profilePicture) {
        return userService.updateUser(userUpdateDto, username, profilePicture);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{username}/enabled")
    public ResponseEntity<Void> toggleUserEnabledStatus(@PathVariable("username") String username) {
        userService.toggleUserEnabledStatus(username);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{username}/locked")
    public ResponseEntity<Void> toggleUserLockedStatus(@PathVariable("username") String username) {
        userService.toggleUserLockedStatus(username);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(path = "/{username}")
    public ResponseEntity<Void> deleteUser(@PathVariable("username") String username) {
        userService.deleteUser(username);
        return ResponseEntity.noContent().build();
    }
}
