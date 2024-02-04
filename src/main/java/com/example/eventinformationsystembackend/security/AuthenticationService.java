package com.example.eventinformationsystembackend.security;

import com.example.eventinformationsystembackend.exception.RefreshTokenHasExpiredException;
import com.example.eventinformationsystembackend.exception.ResourceNotFoundException;
import com.example.eventinformationsystembackend.exception.TokenHasBeenRevokedException;
import com.example.eventinformationsystembackend.model.AuthenticationToken;
import com.example.eventinformationsystembackend.model.RefreshToken;
import com.example.eventinformationsystembackend.model.User;
import com.example.eventinformationsystembackend.repository.AuthenticationTokenRepository;
import com.example.eventinformationsystembackend.repository.RefreshTokenRepository;
import com.example.eventinformationsystembackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.example.eventinformationsystembackend.common.ExceptionMessages.*;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthenticationTokenRepository authenticationTokenRepository;

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

        String refreshTokenString = jwtService.generateRefreshTokenString();

        RefreshToken refreshToken = RefreshToken
                .builder()
                .token(refreshTokenString)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusHours(24))
                .isRevoked(false)
                .user(user)
                .build();

        refreshToken = refreshTokenRepository.save(refreshToken);

        AuthenticationToken authenticationToken = AuthenticationToken
                .builder()
                .token(jwt)
                .isRevoked(false)
                .refreshToken(refreshToken)
                .build();

        authenticationTokenRepository.save(authenticationToken);

        return AuthenticationResponse
                .builder()
                .authenticationToken(jwt)
                .refreshToken(refreshTokenString)
                .build();
    }

    public String refreshToken(String token) {
        System.out.println(token);
        RefreshToken refreshToken = jwtService.getRefreshToken(token);

        if (refreshToken.getIsRevoked()) {
            throw new TokenHasBeenRevokedException(REFRESH_TOKEN_HAS_BEEN_REVOKED);
        }

        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshToken.setIsRevoked(true);
            refreshToken.getAuthenticationToken().setIsRevoked(true);
            authenticationTokenRepository.delete(refreshToken.getAuthenticationToken());
            refreshTokenRepository.delete(refreshToken);
            throw new RefreshTokenHasExpiredException(REFRESH_TOKEN_HAS_EXPIRED);
        }

        User user = userRepository.findUserByUsername(jwtService.getUsernameFromSecurityContext())
                .orElseThrow(() -> new ResourceNotFoundException(USER_DOES_NOT_EXIST));

        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", user.getUserRole().name());

        String authToken = jwtService.generateToken(user, extraClaims);

//        AuthenticationToken authenticationToken = AuthenticationToken
//                .builder()
//                .token(authToken)
//                .isRevoked(false)
//                .refreshToken(refreshToken)
//                .build();

        AuthenticationToken authenticationToken = refreshToken.getAuthenticationToken();
        authenticationToken.setToken(authToken);

        authenticationTokenRepository.save(authenticationToken);

        return authToken;
    }
}
