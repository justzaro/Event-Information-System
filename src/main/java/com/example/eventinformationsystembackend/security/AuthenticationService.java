package com.example.eventinformationsystembackend.security;

import com.example.eventinformationsystembackend.exception.ResourceNotFoundException;
import com.example.eventinformationsystembackend.model.User;
import com.example.eventinformationsystembackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static com.example.eventinformationsystembackend.common.ExceptionMessages.*;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getUsername(),
                        authenticationRequest.getPassword()
                )
        );

        User user = userRepository.findUserByUsername(authenticationRequest.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException(USER_DOES_NOT_EXIST));

        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", user.getUserRole().name());

        String jwt = jwtService.generateToken(user, extraClaims);

        return AuthenticationResponse
                .builder()
                .token(jwt)
                .build();
    }
}
