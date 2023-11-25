package com.example.eventinformationsystembackend.service.implementation;

import com.example.eventinformationsystembackend.exception.EmailAlreadyConfirmedException;
import com.example.eventinformationsystembackend.exception.ResourceNotFoundException;
import com.example.eventinformationsystembackend.model.ConfirmationToken;
import com.example.eventinformationsystembackend.model.User;
import com.example.eventinformationsystembackend.repository.ConfirmationTokenRepository;
import com.example.eventinformationsystembackend.repository.UserRepository;
import com.example.eventinformationsystembackend.service.ConfirmationTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import static com.example.eventinformationsystembackend.common.ExceptionMessages.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConfirmationTokenServiceImpl implements ConfirmationTokenService {
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final UserRepository userRepository;

    private void saveConfirmationToken(ConfirmationToken token) {
        confirmationTokenRepository.save(token);
    }

    @Override
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

    @Override
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

    private void updateTokenConfirmedAtField(String token) {
        confirmationTokenRepository.updateConfirmedAtField(token, LocalDateTime.now());
    }
}
