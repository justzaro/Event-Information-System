package com.example.eventinformationsystembackend.controller;

import com.example.eventinformationsystembackend.dto.UserDto;
import com.example.eventinformationsystembackend.dto.UserDtoResponse;
import com.example.eventinformationsystembackend.service.ConfirmationTokenService;
import com.example.eventinformationsystembackend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

import java.io.IOException;

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
    public String confirmToken(@RequestParam String token) {
        return confirmationTokenService.confirmToken(token);
    }

    @GetMapping(path = "/{username}")
    public UserDtoResponse getUser(@PathVariable("username") String username) {
        return userService.getUser(username);
    }

    @GetMapping(path = "/profile-picture/{username}")
    public ResponseEntity<?> getUserProfilePicture(
            @PathVariable("username") String username) throws IOException {
        byte[] profilePicture = userService.getUserProfilePicture(username);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf("image/png"))
                .body(profilePicture);
    }

    @PostMapping(path = "/register", consumes = { MediaType.APPLICATION_JSON_VALUE,
                                                     MediaType.MULTIPART_FORM_DATA_VALUE })
    public UserDtoResponse registerUser(@Valid @RequestPart UserDto userDto,
                                        @RequestPart(required = false) MultipartFile profilePicture) {
        return userService.registerUser(userDto, profilePicture);
    }

    @PutMapping(path = "/update/{username}")
    public UserDtoResponse updateUser(@PathVariable("username") String username,
                                      @RequestPart @Valid UserDto userDto,
                                      @RequestPart(required = false) MultipartFile profilePicture) {
        return userService.updateUser(userDto, username, profilePicture);
    }

    @DeleteMapping(path = "/delete/{username}")
    public ResponseEntity<Void> deleteUser(@PathVariable("username") String username) {
        userService.deleteUser(username);
        return ResponseEntity.noContent().build();
    }
}
