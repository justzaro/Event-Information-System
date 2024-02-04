package com.example.eventinformationsystembackend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "authentication_tokens")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthenticationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "auth_token_id")
    private Long id;

    @Column(name = "token", nullable = false)
    private String token;

    @Column(name = "is_revoked", nullable = false)
    private Boolean isRevoked;

    @OneToOne
    @JoinColumn(name = "refresh_token_id", referencedColumnName = "refresh_token_id", nullable = false)
    private RefreshToken refreshToken;
}
