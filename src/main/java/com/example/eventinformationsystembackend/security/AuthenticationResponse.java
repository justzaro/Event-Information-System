package com.example.eventinformationsystembackend.security;

import com.example.eventinformationsystembackend.model.AuthenticationToken;
import com.example.eventinformationsystembackend.model.RefreshToken;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {
    private String authenticationToken;
    private String refreshToken;
}
