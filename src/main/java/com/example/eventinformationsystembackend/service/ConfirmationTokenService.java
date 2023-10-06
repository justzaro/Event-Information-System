package com.example.eventinformationsystembackend.service;

import com.example.eventinformationsystembackend.exception.EmailAlreadyConfirmedException;
import com.example.eventinformationsystembackend.exception.ResourceNotFoundException;
import com.example.eventinformationsystembackend.model.ConfirmationToken;
import com.example.eventinformationsystembackend.model.User;
import com.example.eventinformationsystembackend.repository.ConfirmationTokenRepository;
import com.example.eventinformationsystembackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import static com.example.eventinformationsystembackend.common.ExceptionMessages.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class ConfirmationTokenService {
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final UserRepository userRepository;

    @Autowired
    public ConfirmationTokenService(ConfirmationTokenRepository confirmationTokenRepository,
                                    UserRepository userRepository) {
        this.confirmationTokenRepository = confirmationTokenRepository;
        this.userRepository = userRepository;
    }

    public void saveConfirmationToken(ConfirmationToken token) {
        confirmationTokenRepository.save(token);
    }

    public String createToken(User user) {
        String token = UUID.randomUUID().toString();

        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(10),
                user
        );

        saveConfirmationToken(confirmationToken);
        return token;
    }

    public boolean confirmToken(String token) {

        ConfirmationToken confirmationToken = confirmationTokenRepository.
                findConfirmationTokenByToken(token)
                .orElseThrow(() ->
                        new ResourceNotFoundException(TOKEN_DOES_NOT_EXIST));

        if (confirmationToken.getConfirmedAt() != null) {
            throw new EmailAlreadyConfirmedException(EMAIL_ALREADY_CONFIRMED);
        }

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new ResourceNotFoundException(TOKEN_HAS_EXPIRED);
        }

        updateTokenConfirmedAtField(token);
        userRepository.updateIsEnabledColumn(confirmationToken.getUser().getUsername());

        return true;
    }

    public void updateTokenConfirmedAtField(String token) {
        confirmationTokenRepository.updateConfirmedAtField(token, LocalDateTime.now());
    }
}
