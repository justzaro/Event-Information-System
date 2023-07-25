package com.example.eventinformationsystembackend.controller;

import com.example.eventinformationsystembackend.dto.UserDto;
import com.example.eventinformationsystembackend.dto.UserDtoResponse;
import com.example.eventinformationsystembackend.service.ConfirmationTokenService;
import com.example.eventinformationsystembackend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping(path = "/register")
    public UserDtoResponse registerUser(@Valid @RequestBody UserDto userDto) {
        return userService.registerUser(userDto);
    }

}
