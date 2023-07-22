package com.example.eventinformationsystembackend.service;

import com.example.eventinformationsystembackend.common.enums.UserRole;
import com.example.eventinformationsystembackend.dto.UserDto;
import com.example.eventinformationsystembackend.dto.UserDtoResponse;
import com.example.eventinformationsystembackend.model.User;
import com.example.eventinformationsystembackend.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;
import org.springframework.ui.Model;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;


    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.modelMapper = new ModelMapper();
    }

    public UserDtoResponse getUser(String username) {
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new IllegalStateException("no user"));

        return modelMapper.map(user, UserDtoResponse.class);
    }

    public UserDtoResponse registerUser(UserDto userDto) {
        if (userRepository.findUserByUsername(userDto.getUsername()).isPresent()) {
            throw new IllegalStateException("username taken");
        }

        if (userRepository.findUserByEmail(userDto.getUsername()).isPresent()) {
            throw new IllegalStateException("email taken");
        }

        if (userRepository.findUserByPhoneNumber(userDto.getUsername()).isPresent()) {
            throw new IllegalStateException("phone number taken");
        }

        User userToRegister = modelMapper.map(userDto, User.class);
        userToRegister.setUserRole(UserRole.USER);
        userToRegister.setIsEnabled(false);
        userToRegister.setIsLocked(false);
        userToRegister.setProfilePicturePath("asd");

        return modelMapper.map(userRepository.save(userToRegister), UserDtoResponse.class);
    }
}
