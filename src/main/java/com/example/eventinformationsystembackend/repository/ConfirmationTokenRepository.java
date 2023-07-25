package com.example.eventinformationsystembackend.repository;

import com.example.eventinformationsystembackend.model.ConfirmationToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ConfirmationTokenRepository
        extends JpaRepository<ConfirmationToken, Long> {
    Optional<ConfirmationToken> findConfirmationTokenByToken(String token);

    @Transactional
    @Modifying
    @Query(value = "UPDATE confirmation_tokens SET confirmed_at = :confirmedAt WHERE token = :token",
           nativeQuery = true)
    void updateConfirmedAtField(@Param("token") String token,
                                @Param("confirmedAt") LocalDateTime confirmedAt);
}
