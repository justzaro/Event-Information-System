package com.example.eventinformationsystembackend.service;

import com.example.eventinformationsystembackend.model.User;

public interface ConfirmationTokenService {
    boolean confirmToken(String token);

    String createToken(User user);
}
